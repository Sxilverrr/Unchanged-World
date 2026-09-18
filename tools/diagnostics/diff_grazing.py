import sys, collections, math, os
sys.path.insert(0, os.path.join(os.path.dirname(os.path.abspath(__file__)), '..'))
import compare_worlds as cw
la, ca = cw.load_world(sys.argv[1]); lb, cb = cw.load_world(sys.argv[2])
NAMES = {0: 'air', 2: 'grass', 3: 'dirt', 8: 'fwater', 9: 'water', 31: 'tallgrass', 78: 'snow', 79: 'ice'}
def entities(chunks):
    out = []
    for lvl in chunks.values():
        for e in lvl.get('Entities', []) or []:
            out.append((e.get('id'), tuple(e.get('Pos', (0, 0, 0)))))
    return out
ea, eb = entities(ca), entities(cb)
print('entities 1.6.4:', collections.Counter(i for i, _ in ea).most_common(6))
print('entities mod:  ', collections.Counter(i for i, _ in eb).most_common(6))
def near(es, p):
    return sorted((round(math.dist(q, p), 1), i) for i, q in es if abs(q[0] - p[0]) < 24 and abs(q[2] - p[2]) < 24)[:2]
total = 0
for key in sorted(set(ca) & set(cb)):
    if not (ca[key].get('TerrainPopulated', 0) and cb[key].get('TerrainPopulated', 0)):
        continue
    sa, sb = cw.sections(ca[key]), cw.sections(cb[key])
    for y in sorted(set(sa) | set(sb)):
        ba = sa.get(y, (bytes(4096), b''))[0]; bb = sb.get(y, (bytes(4096), b''))[0]
        for i in range(4096):
            if ba[i] != bb[i]:
                total += 1
                p = (key[0] * 16 + (i & 15), y * 16 + (i >> 8), key[1] * 16 + ((i >> 4) & 15))
                print('%-9s %-24s %-9s -> %-9s  1.6.4 near %-28s mod near %s' % (key, p, NAMES.get(ba[i], ba[i]), NAMES.get(bb[i], bb[i]), near(ea, p), near(eb, p)))
print('total block diffs:', total)
