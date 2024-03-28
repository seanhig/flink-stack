JARDIR=./jars/
CURRENT_DIR=$PWD

rm -rf $JARDIR
mkdir -p $JARDIR

cd $JARDIR
FLINK_CDC_VER=3.0.0
wget https://github.com/ververica/flink-cdc-connectors/releases/download/release-$FLINK_CDC_VER/flink-cdc-$FLINK_CDC_VER-bin.tar.gz
tar -xvf *.gz
cp flink-cdc-$FLINK_CDC_VER/lib/flink-cdc-dist-$FLINK_CDC_VER.jar .
rm -rf flink-cdc-$FLINK_CDC_VER
rm *.gz

cd $CURRENT_DIR
mvn dependency:copy-dependencies -DoutputDirectory=$JARDIR

