import sys, collections
import os
sys.path.insert(0, os.path.join(os.path.dirname(os.path.abspath(__file__)), '..'))
import compare_worlds as cw
la, ca = cw.load_world(sys.argv[1]); lb, cb = cw.load_world(sys.argv[2])
NAMES = {0:'air',1:'stone',2:'grass',3:'dirt',4:'cobble',5:'planks',7:'bedrock',8:'fwater',9:'water',10:'flava',11:'lava',12:'sand',13:'gravel',17:'log',18:'leaves',24:'sandstone',31:'tallgrass',32:'deadbush',37:'dandelion',38:'rose',39:'brownmush',40:'redmush',48:'mossy',50:'torch',52:'spawner',53:'oakstairs',54:'chest',64:'door',65:'ladder',66:'rail',67:'cobstairs',78:'snow',79:'ice',81:'cactus',82:'clay',83:'reed',85:'fence',86:'pumpkin',97:'silverfish',99:'hugebrown',100:'hugered',106:'vine',111:'lily',129:'emerald',16:'coal',15:'iron',14:'gold',21:'lapis',56:'diamond',73:'redstone',30:'web',35:'wool',59:'wheat',60:'farmland',141:'carrot',142:'potato',20:'glass',89:'glowstone',43:'dslab',44:'slab',98:'stonebrick',109:'sbstairs',101:'ironbars',102:'pane',71:'irondoor',77:'button',70:'plate',72:'wplate',96:'trapdoor',146:'trapchest',132:'tripwire',131:'hook',23:'dispenser',33:'piston',29:'spiston',69:'lever',55:'redwire',76:'rtorch',75:'rtorchoff'}
def analyze(keys, label):
    pairs = collections.Counter(); ys = collections.Counter(); n = 0
    for key in keys:
        a, b = ca[key], cb[key]
        sa, sb = cw.sections(a), cw.sections(b)
        for y in set(sa) | set(sb):
            ba = sa.get(y, (bytes(4096), b''))[0]; bb = sb.get(y, (bytes(4096), b''))[0]
            for i in range(4096):
                if ba[i] != bb[i]:
                    n += 1; pairs[(ba[i], bb[i])] += 1; ys[(y * 16 + (i >> 8)) // 8 * 8] += 1
    print('== %s: %d diffs' % (label, n))
    for (pa, pb), c in pairs.most_common(12): print('   %-10s -> %-10s %d' % (NAMES.get(pa, pa), NAMES.get(pb, pb), c))
    print('   y:', ' '.join('%d:%d' % (y, ys[y]) for y in sorted(ys)))
common = sorted(set(ca) & set(cb))
bad = [k for k in common if ca[k].get('TerrainPopulated', 0) and cb[k].get('TerrainPopulated', 0) and cw.sections(ca[k]) != cw.sections(cb[k])]
analyze(bad, 'ALL %d mismatching chunks' % len(bad))
for k in [(-13, 8), (9, 0), (-13, -5)]:
    if k in ca and k in cb: analyze([k], 'chunk %s' % (k,))
xs = sorted(set(k[0] for k in bad)); zs = sorted(set(k[1] for k in bad))
grid = [['.' if (x, z) not in bad else '#' for x in range(min(xs), max(xs) + 1)] for z in range(min(zs), max(zs) + 1)]
print('mismatch map (x %d..%d left->right, z %d..%d top->bottom):' % (min(xs), max(xs), min(zs), max(zs)))
for row in grid: print('  ' + ''.join(row))
