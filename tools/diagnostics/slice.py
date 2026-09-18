import sys
import os
sys.path.insert(0, os.path.join(os.path.dirname(os.path.abspath(__file__)), '..'))
import compare_worlds as cw
worlds = [('1.6.4', cw.load_world(sys.argv[1])[1]), ('mod', cw.load_world(sys.argv[2])[1])]
SYM = {0:'.', 1:'#', 2:'g', 3:'d', 9:'~', 8:'~', 11:'L', 10:'L', 12:'s', 13:'v', 17:'T', 18:'l', 31:'"', 106:'|', 7:'B', 24:'S', 16:'c', 15:'i', 82:'C', 37:'f', 38:'f', 78:'*', 79:'I', 4:'k', 48:'m', 99:'M', 100:'M'}
def cell(ch, x, y, z):
    key = (x >> 4, z >> 4)
    if key not in ch: return '?', 0
    sec = {s['Y']: s for s in ch[key].get('Sections', [])}.get(y >> 4)
    if not sec: return '.', 15
    i = ((y & 15) << 8) | ((z & 15) << 4) | (x & 15)
    b = sec['Blocks'][i]; sl = (sec['SkyLight'][i >> 1] >> ((i & 1) * 4)) & 15
    return SYM.get(b, '?'), sl
x0, y0, z0 = (int(v) for v in sys.argv[3].split(','))
for name, ch in worlds:
    print('== %s: x from %d..%d at z=%d, y from %d down to %d (block / skylight hex)' % (name, x0 - 6, x0 + 6, z0, y0 + 6, y0 - 4))
    for y in range(y0 + 6, y0 - 5, -1):
        row = []
        for x in range(x0 - 6, x0 + 7):
            b, sl = cell(ch, x, y, z0)
            row.append('%s%x' % (b, sl))
        print('   y=%3d  %s' % (y, ' '.join(row)))
