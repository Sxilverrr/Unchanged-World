import sys, collections
import os
sys.path.insert(0, os.path.join(os.path.dirname(os.path.abspath(__file__)), '..'))
import compare_worlds as cw
la, ca = cw.load_world(sys.argv[1]); lb, cb = cw.load_world(sys.argv[2])
NAMES = {0:'air',1:'stone',2:'grass',3:'dirt',4:'cobble',5:'planks',7:'bedrock',8:'fwater',9:'water',10:'flava',11:'lava',12:'sand',13:'gravel',17:'log',18:'leaves',24:'sandstone',31:'tallgrass',32:'deadbush',37:'dandelion',38:'rose',39:'brownmush',40:'redmush',48:'mossy',50:'torch',52:'spawner',54:'chest',78:'snow',79:'ice',81:'cactus',82:'clay',83:'reed',86:'pumpkin',97:'silverfish',106:'vine',111:'lily',129:'emerald',16:'coal',15:'iron',14:'gold',21:'lapis',56:'diamond',73:'redstone',30:'web',99:'hugebrown',100:'hugered'}
for arg in sys.argv[3:]:
    key = tuple(int(v) for v in arg.split(','))
    a, b = ca[key], cb[key]
    sa, sb = cw.sections(a), cw.sections(b)
    out = []
    for y in sorted(set(sa) | set(sb)):
        ba, da = sa.get(y, (bytes(4096), bytes(2048))); bb, db = sb.get(y, (bytes(4096), bytes(2048)))
        for i in range(4096):
            if ba[i] != bb[i]:
                out.append((i & 15, y * 16 + (i >> 8), (i >> 4) & 15, NAMES.get(ba[i], ba[i]), NAMES.get(bb[i], bb[i])))
    print('== chunk %s: %d diffs, biome %s' % (key, len(out), collections.Counter(bytes(a.get('Biomes', b''))).most_common(2)))
    pairs = collections.Counter((o[3], o[4]) for o in out)
    for p, n in pairs.most_common(8): print('   %-10s -> %-10s %d' % (p[0], p[1], n))
    for o in out[:6]: print('   at local x=%d y=%d z=%d: %s -> %s' % o)
