import sys, collections
import os
sys.path.insert(0, os.path.join(os.path.dirname(os.path.abspath(__file__)), '..'))
import compare_worlds as cw
la, ca = cw.load_world(sys.argv[1]); lb, cb = cw.load_world(sys.argv[2])
def lights(level):
    out = {}
    for s in level.get('Sections', []):
        out[s['Y']] = (bytes(s.get('SkyLight', b'')), bytes(s.get('BlockLight', b'')), bytes(s.get('Blocks', b'')))
    return out
def nib(arr, i): return (arr[i >> 1] >> ((i & 1) * 4)) & 15 if arr else 0
common = sorted(set(ca) & set(cb))
sky_bad = collections.Counter(); blk_bad = collections.Counter(); chunks_sky = 0; chunks_blk = 0; ys = collections.Counter()
examples = []
for key in common:
    A, B = lights(ca[key]), lights(cb[key])
    s_diff = b_diff = 0
    for y in set(A) | set(B):
        sa, ba, blocks_a = A.get(y, (b'', b'', b'')); sb, bb, blocks_b = B.get(y, (b'', b'', b''))
        if not sa and not sb: continue
        for i in range(4096):
            va, vb = nib(sa, i), nib(sb, i)
            if va != vb:
                s_diff += 1; sky_bad[(va, vb)] += 1; ys[(y * 16 + (i >> 8)) // 8 * 8] += 1
                if len(examples) < 8 and blocks_a[i:i+1] == blocks_b[i:i+1]: examples.append((key, i & 15, y * 16 + (i >> 8), (i >> 4) & 15, 'sky', va, vb, blocks_a[i] if blocks_a else -1))
            wa, wb = nib(ba, i), nib(bb, i)
            if wa != wb:
                b_diff += 1; blk_bad[(wa, wb)] += 1
    chunks_sky += s_diff > 0; chunks_blk += b_diff > 0
print('chunks with SkyLight diffs: %d / %d; BlockLight diffs: %d' % (chunks_sky, len(common), chunks_blk))
print('sky pairs (1.6.4, mod):', sky_bad.most_common(10))
print('block-light pairs:', blk_bad.most_common(6))
print('sky diff y histogram:', sorted(ys.items()))
for e in examples: print('  sky diff at same block:', e)
