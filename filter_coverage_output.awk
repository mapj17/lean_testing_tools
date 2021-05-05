#!/usr/bin/awk -f
BEGIN{numSeparators = 0}
/---/ {numSeparators += 1}
(numSeparators == 1) && (/[^-]{3}/) {print $0}
