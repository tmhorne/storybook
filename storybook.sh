#!/bin/sh

echo "starting Storybook ..."
java -Xmx256m -jar lib/storybook.jar $*
echo "done."
