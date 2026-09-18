import sys
import os
sys.path.insert(0, os.path.join(os.path.dirname(os.path.abspath(__file__)), '..'))
import compare_worlds as cw
NAMES = {0:'air',1:'stone',2:'grass',3:'dirt',17:'log',18:'leaves',31:'tallgrass',106:'vine',9:'water',8:'fwater',12:'sand',13:'gravel',37:'dand',38:'rose',127:'cocoa',39:'bmush',40:'rmush'}
worlds = [('1.6.4', cw.load_world(sys.argv[1])[1]), ('mod', cw.load_world(sys.argv[2])[1])]
def block(ch, x, y, z):
    key = (x >> 4, z >> 4)
    if key not in ch: return '?'
    sec = cw.sections(ch[key]).get(y >> 4)
    if not sec: return 'air'
    i = ((y & 15) << 8) | ((z & 15) << 4) | (x & 15)
    b = sec[0][i]; m = (sec[1][i >> 1] >> ((i & 1) * 4)) & 15
    return '%s:%d' % (NAMES.get(b, b), m)
for arg in sys.argv[3:]:
    x, y, z = (int(v) for v in arg.split(','))
    print('== around (%d,%d,%d)' % (x, y, z))
    for name, ch in worlds:
        print('  %-6s here=%-10s x-1=%-10s x+1=%-10s z-1=%-10s z+1=%-10s y-1=%-10s y+1=%-10s' % (name, block(ch,x,y,z), block(ch,x-1,y,z), block(ch,x+1,y,z), block(ch,x,y,z-1), block(ch,x,y,z+1), block(ch,x,y-1,z), block(ch,x,y+1,z)))
