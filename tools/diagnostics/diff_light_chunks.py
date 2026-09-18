import sys, collections
import os
sys.path.insert(0, os.path.join(os.path.dirname(os.path.abspath(__file__)), '..'))
import compare_worlds as cw
la, ca = cw.load_world(sys.argv[1]); lb, cb = cw.load_world(sys.argv[2])
def lights(level):
    return {s['Y']: (bytes(s.get('SkyLight', b'')), bytes(s.get('Blocks', b''))) for s in level.get('Sections', [])}
def nib(arr, i): return (arr[i >> 1] >> ((i & 1) * 4)) & 15 if arr else 0
for key in sorted(set(ca) & set(cb)):
    A, B = lights(ca[key]), lights(cb[key])
    pairs = collections.Counter(); ys = []; secs_a = sorted(A); secs_b = sorted(B)
    for y in set(A) | set(B):
        sa, _ = A.get(y, (b'', b'')); sb, _ = B.get(y, (b'', b''))
        for i in range(4096):
            va, vb = nib(sa, i), nib(sb, i)
            if va != vb: pairs[(va, vb)] += 1; ys.append(y * 16 + (i >> 8))
    if pairs:
        biome = collections.Counter(bytes(ca[key].get('Biomes', b''))).most_common(1)[0][0]
        print('chunk %-9s biome %2d pop %d: %5d sky diffs, y %d-%d, sections 1.6.4=%s mod=%s, top pairs %s' % (key, biome, ca[key].get('TerrainPopulated', 0), sum(pairs.values()), min(ys), max(ys), secs_a, secs_b, pairs.most_common(3)))
