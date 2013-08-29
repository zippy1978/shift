#!/bin/sh
# Script in charge of building documentation from source

# Developer guide ----------

# HTML
pandoc -s -S \
--number-sections \
--toc \
-c css/html.css \
developer_guide.md \
-o developer_guide.html

# PDF (with preamble)
FONT_SIZE=12pt
FONT_NAME="Helvetica Neue Light"
CODE_FONT_NAME="Source Code Pro"
pandoc developer_guide_preamble.md -o developer_guide_preamble.tex
pandoc -s -S \
--include-before-body=developer_guide_preamble.tex \
--variable mainfont="$FONT_NAME" \
--variable fontsize=$FONT_SIZE \
--variable monofont="$CODE_FONT_NAME" \
--variable papersize:"a4paper" \
--variable geometry:margin=2cm \
--variable version=0.1 \
--latex-engine=xelatex \
--chapters \
--number-sections \
--toc \
developer_guide.md \
-o developer_guide.pdf
