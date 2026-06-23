from cairosvg import svg2png
import os

base = os.path.dirname(__file__)
svg = os.path.join(base, 'architecture-diagram.svg')
png = os.path.join(base, 'architecture-diagram.png')

print('SVG:', svg)
print('PNG:', png)
svg2png(url=svg, write_to=png)
print('Converted SVG to PNG')
