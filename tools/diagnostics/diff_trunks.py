import sys
import os
sys.path.insert(0, os.path.join(os.path.dirname(os.path.abspath(__file__)), '..'))
import compare_worlds as cw
la, ca = cw.load_world(sys.argv[1]); lb, cb = cw.load_world(sys.argv[2])
def blocks(level):
    sec = cw.sections(level); out = {}
    for y, (b, d) in sec.items(): out[y] = (b, d)
    return out
def get(sec, x, y, z):
    s = sec.get(y >> 4)
    if not s: return 0, 0
    i = ((y & 15) << 8) | (z << 4) | x
    return s[0][i], (s[1][i >> 1] >> ((i & 1) * 4)) & 15
def trunks(level):
    sec = blocks(level); found = []
    for y in range(1, 128):
        for z in range(16):
            for x in range(16):
                b, m = get(sec, x, y, z)
                if b == 17:
                    below, _ = get(sec, x, y - 1, z)
                    if below != 17:
                        h = 0
                        while get(sec, x, y + h, z)[0] == 17: h += 1
                        found.append((x, y, z, m & 3, h, below))
    return sorted(found)
for arg in sys.argv[3:]:
    key = tuple(int(v) for v in arg.split(','))
    ta, tb = trunks(ca[key]), trunks(cb[key])
    sa, sb = set(ta), set(tb)
    print('== chunk %s: trunk bases 1.6.4=%d mod=%d (x,y,z,woodmeta,height,blockBelow)' % (key, len(ta), len(tb)))
    print('   only in 1.6.4:', sorted(sa - sb)[:12])
    print('   only in mod  :', sorted(sb - sa)[:12])
