#!/bin/bash

EXRS="exrs"
SOLS="sols"

recurse() {
 for i in $@; do
    if [ -d "$i" ];then
        recurse "$i/*"
    elif [ -f "$i" ]; then
        DIR=$(dirname $i)
        mkdir -p $EXRS/$DIR
        mkdir -p $SOLS/$DIR
        CS="$SOLS/$i"
        EX="$EXRS/$i"
        cp $i $CS
        cp $i $EX

        perl -pi -e 'BEGIN{undef $/;} s/\n\s*\/\/<sol>.*?<\/sol>//smg' $EX
        perl -pi -e 'BEGIN{undef $/;} s/\/\/\///g' $EX

        perl -pi -e 'BEGIN{undef $/;} s/.*<sol>\n//g' $CS
        perl -pi -e 'BEGIN{undef $/;} s/.*<\/sol>\n//g' $CS
        perl -pi -e 'BEGIN{undef $/;} s/.*\/\/\/.*\n//g' $CS

    fi
 done
}

recurse $@
