
for d in */ ; do
    echo "building jar pack for ${d}"
    cd $d
    ./build.sh
    cd ..
done

echo
echo "Build all jar-packs complete!"
echo