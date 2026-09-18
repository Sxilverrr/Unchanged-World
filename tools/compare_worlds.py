#!/usr/bin/env python3
"""Compare two Anvil worlds chunk by chunk: spawn point, biome arrays and block arrays.

Usage: compare_worlds.py <world A dir> <world B dir> [--verbose]
Both directories must contain level.dat and region/. Works for 1.6.4 and 1.7.10 saves.
"""
import glob
import gzip
import os
import struct
import sys
import zlib


def read_nbt(data):
    pos = [0]

    def rd(fmt):
        value = struct.unpack_from(fmt, data, pos[0])[0]
        pos[0] += struct.calcsize(fmt)
        return value

    def read_string():
        n = rd('>H')
        s = data[pos[0]:pos[0] + n].decode('utf-8', 'replace')
        pos[0] += n
        return s

    def read_payload(t):
        if t == 1:
            return rd('>b')
        if t == 2:
            return rd('>h')
        if t == 3:
            return rd('>i')
        if t == 4:
            return rd('>q')
        if t == 5:
            return rd('>f')
        if t == 6:
            return rd('>d')
        if t == 7:
            n = rd('>i')
            b = data[pos[0]:pos[0] + n]
            pos[0] += n
            return b
        if t == 8:
            return read_string()
        if t == 9:
            lt = rd('>b')
            n = rd('>i')
            return [read_payload(lt) for _ in range(n)]
        if t == 10:
            d = {}
            while True:
                tt = rd('>b')
                if tt == 0:
                    return d
                name = read_string()
                d[name] = read_payload(tt)
        if t == 11:
            n = rd('>i')
            v = list(struct.unpack_from('>%di' % n, data, pos[0]))
            pos[0] += 4 * n
            return v
        if t == 12:
            n = rd('>i')
            v = list(struct.unpack_from('>%dq' % n, data, pos[0]))
            pos[0] += 8 * n
            return v
        raise ValueError('unknown tag %d' % t)

    t = rd('>b')
    read_string()
    return read_payload(t)


def read_region(path):
    chunks = {}
    with open(path, 'rb') as f:
        data = f.read()
    for i in range(1024):
        offset = int.from_bytes(data[i * 4:i * 4 + 3], 'big')
        if offset == 0:
            continue
        start = offset * 4096
        length = int.from_bytes(data[start:start + 4], 'big')
        compression = data[start + 4]
        raw = data[start + 5:start + 4 + length]
        if compression == 2:
            raw = zlib.decompress(raw)
        elif compression == 1:
            raw = gzip.decompress(raw)
        else:
            continue
        level = read_nbt(raw)['Level']
        chunks[(level['xPos'], level['zPos'])] = level
    return chunks


def load_world(path):
    chunks = {}
    for region in glob.glob(os.path.join(path, 'region', 'r.*.mca')):
        chunks.update(read_region(region))
    with gzip.open(os.path.join(path, 'level.dat'), 'rb') as f:
        level = read_nbt(f.read())['Data']
    return level, chunks


def sections(level):
    out = {}
    for s in level.get('Sections', []):
        out[s['Y']] = (bytes(s.get('Blocks', b'')), bytes(s.get('Data', b'')))
    return out


def count_diff(a, b):
    n = min(len(a), len(b))
    return sum(1 for i in range(n) if a[i] != b[i]) + abs(len(a) - len(b))


TERRAIN_IDS = {0, 1, 2, 3, 7, 8, 9, 10, 11, 24, 79}


def count_terrain_diff(a, b):
    n = min(len(a), len(b))
    return sum(1 for i in range(n) if a[i] != b[i] and a[i] in TERRAIN_IDS and b[i] in TERRAIN_IDS)


STRICT_TERRAIN_IDS = {0, 1, 2, 7, 9, 11, 12, 24, 79}


def compare_pristine_ring(chunks_a, chunks_b, common):
    unpopulated = [k for k in common
                   if not chunks_a[k].get('TerrainPopulated', 0) and not chunks_b[k].get('TerrainPopulated', 0)]
    if not unpopulated:
        return 0, 0
    max_x = max(k[0] for k in unpopulated)
    max_z = max(k[1] for k in unpopulated)
    checked = 0
    bad = 0
    for key in unpopulated:
        sa = sections(chunks_a[key])
        sb = sections(chunks_b[key])
        for y in set(sa) | set(sb):
            ba = sa.get(y, (bytes(4096), b''))[0]
            bb = sb.get(y, (bytes(4096), b''))[0]
            for i in range(4096):
                lx = i & 15
                lz = (i >> 4) & 15
                if not ((key[0] == max_x and lx >= 9) or (key[1] == max_z and lz >= 9)):
                    continue
                if ba[i] in STRICT_TERRAIN_IDS and bb[i] in STRICT_TERRAIN_IDS:
                    checked += 1
                    if ba[i] != bb[i]:
                        bad += 1
    return checked, bad


def describe(name, level):
    print('%s: seed %s, generator %r, spawn (%s, %s, %s)' % (
        name, level.get('RandomSeed'), level.get('generatorName'),
        level.get('SpawnX'), level.get('SpawnY'), level.get('SpawnZ')))


def main(argv):
    if len(argv) < 3:
        print(__doc__)
        return 2
    verbose = '--verbose' in argv
    level_a, chunks_a = load_world(argv[1])
    level_b, chunks_b = load_world(argv[2])
    describe('A', level_a)
    describe('B', level_b)
    common = sorted(set(chunks_a) & set(chunks_b))
    print('chunks: A %d, B %d, common %d' % (len(chunks_a), len(chunks_b), len(common)))

    biome_bad = []
    block_bad = []
    terrain_bad = []
    populated = 0
    for key in common:
        a = chunks_a[key]
        b = chunks_b[key]
        biomes_a = bytes(a.get('Biomes', b''))
        biomes_b = bytes(b.get('Biomes', b''))
        if biomes_a != biomes_b:
            biome_bad.append((key, count_diff(biomes_a, biomes_b)))
        if a.get('TerrainPopulated', 0) and b.get('TerrainPopulated', 0):
            populated += 1
            sa = sections(a)
            sb = sections(b)
            blocks = 0
            data = 0
            terrain = 0
            for y in set(sa) | set(sb):
                ba, da = sa.get(y, (bytes(4096), bytes(2048)))
                bb, db = sb.get(y, (bytes(4096), bytes(2048)))
                blocks += count_diff(ba, bb)
                data += count_diff(da, db)
                terrain += count_terrain_diff(ba, bb)
            if blocks or data:
                block_bad.append((key, blocks, data))
            if terrain:
                terrain_bad.append((key, terrain))

    pristine_checked, pristine_bad = compare_pristine_ring(chunks_a, chunks_b, common)

    limit = None if verbose else 10
    print('biome arrays: %d of %d common chunks identical' % (len(common) - len(biome_bad), len(common)))
    print('raw terrain in never-populated ring columns: %d of %d blocks identical' % (pristine_checked - pristine_bad, pristine_checked))
    for key, n in biome_bad[:limit]:
        print('  biome mismatch at chunk %s: %d of 256 cells differ' % (key, n))
    print('terrain blocks (stone/dirt/grass/bedrock/water/lava/sandstone/ice/air only): %d of %d populated chunks identical'
          % (populated - len(terrain_bad), populated))
    for key, n in terrain_bad[:limit]:
        print('  terrain mismatch at chunk %s: %d blocks differ' % (key, n))
    print('all blocks: %d of %d populated common chunks identical' % (populated - len(block_bad), populated))
    for key, blocks, data in block_bad[:limit]:
        print('  block mismatch at chunk %s: %d block ids, %d metadata nibbles differ' % (key, blocks, data))
    return 0 if not biome_bad and not block_bad else 1


if __name__ == '__main__':
    sys.exit(main(sys.argv))
