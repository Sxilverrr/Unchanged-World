import sys, collections, os
sys.path.insert(0, os.path.join(os.path.dirname(os.path.abspath(__file__)), '..'))
import compare_worlds as cw
la, ca = cw.load_world(sys.argv[1]); lb, cb = cw.load_world(sys.argv[2])
NAMES = {0:'air',1:'stone',2:'grass',3:'dirt',8:'fwater',9:'water',10:'flava',11:'lava',12:'sand',13:'gravel',31:'tallgrass',18:'leaves',17:'log',37:'dandelion',38:'rose',39:'brownmush',40:'redmush',78:'snow',79:'ice',16:'coal',15:'iron',73:'redstone',81:'cactus',83:'reed',106:'vine',111:'lily',59:'wheat',60:'farmland',86:'pumpkin',32:'deadbush',6:'sapling',50:'torch',54:'chest',52:'spawner',48:'mossy',4:'cobble',49:'obsidian',24:'sandstone',82:'clay',14:'gold',21:'lapis',56:'diamond'}
populated = [k for k in ca if ca[k].get('TerrainPopulated', 0)]
x0, x1 = min(k[0] for k in populated) + 1, max(k[0] for k in populated)
z0, z1 = min(k[1] for k in populated) + 1, max(k[1] for k in populated)
inner = [k for k in set(ca) & set(cb) if x0 <= k[0] <= x1 and z0 <= k[1] <= z1 and cb[k].get('TerrainPopulated', 0)]
pairs = collections.Counter(); ys = collections.Counter(); bad = 0; per = {}
for k in inner:
    sa, sb = cw.sections(ca[k]), cw.sections(cb[k]); n = 0
    for y in set(sa) | set(sb):
        ba = sa.get(y, (bytes(4096), b''))[0]; bb = sb.get(y, (bytes(4096), b''))[0]
        for i in range(4096):
            if ba[i] != bb[i]:
                pairs[(ba[i], bb[i])] += 1; ys[(y * 16 + (i >> 8)) // 16 * 16] += 1; n += 1
    per[k] = n; bad += n > 0
print('inner area x %d..%d z %d..%d: %d chunks, %d with diffs, %d block diffs' % (x0, x1, z0, z1, len(inner), bad, sum(pairs.values())))
for (a, b), c in pairs.most_common(20): print('   %-10s -> %-10s %d' % (NAMES.get(a, a), NAMES.get(b, b), c))
print('   y:', ' '.join('%d:%d' % (y, ys[y]) for y in sorted(ys)))
print('   worst chunks:', sorted(per.items(), key=lambda t: -t[1])[:6])
