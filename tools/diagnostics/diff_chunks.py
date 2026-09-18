import sys, collections
import os
sys.path.insert(0, os.path.join(os.path.dirname(os.path.abspath(__file__)), '..'))
import compare_worlds as cw
la, ca = cw.load_world(sys.argv[1]); lb, cb = cw.load_world(sys.argv[2])
ORES = {14, 15, 16, 21, 56, 73}
STRUCT = {5:'planks', 85:'fence', 66:'rail', 30:'web', 48:'mossy', 50:'torch', 98:'stonebrick', 54:'chest', 52:'spawner', 64:'door', 53:'oakstairs', 67:'cobstairs', 4:'cobble', 24:'sandstone', 128:'sandstairs', 23:'dispenser', 132:'tripwire', 35:'wool', 60:'farmland'}
common = sorted(set(ca) & set(cb))
rows = []
for key in common:
    a, b = ca[key], cb[key]
    if not (a.get('TerrainPopulated', 0) and b.get('TerrainPopulated', 0)): continue
    sa, sb = cw.sections(a), cw.sections(b)
    if sa == sb: continue
    ore_shift = 0; total = 0; struct = collections.Counter()
    for y in set(sa) | set(sb):
        ba = sa.get(y, (bytes(4096), b''))[0]; bb = sb.get(y, (bytes(4096), b''))[0]
        for i in range(4096):
            if ba[i] in STRUCT: struct[STRUCT[ba[i]]] += 1
            if ba[i] != bb[i]:
                total += 1
                if (ba[i] in ORES and bb[i] == 1) or (bb[i] in ORES and ba[i] == 1): ore_shift += 1
    biome = collections.Counter(bytes(a.get('Biomes', b''))).most_common(1)[0][0]
    rows.append((key, biome, total, ore_shift, dict(struct.most_common(4))))
rows.sort(key=lambda r: (r[0][1], r[0][0]))
print('%-10s %5s %6s %6s  structure blocks in 1.6.4 chunk' % ('chunk', 'biome', 'diffs', 'ores'))
for key, biome, total, ore_shift, struct in rows:
    print('%-10s %5d %6d %6d  %s' % (key, biome, total, ore_shift, struct))
