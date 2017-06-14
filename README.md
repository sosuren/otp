OTP
===========

Setup:

1. Download [OTP jar](https://repo1.maven.org/maven2/org/opentripplanner/otp/1.1.0/otp-1.1.0-shaded.jar) and put inside `.[Project-base]/lib` directory

1. Download [Portland GTFS data](http://developer.trimet.org/schedule/gtfs.zip) and put inside `/var/otp/graphs/pdx` directory

1. Download [OpenStreetMap data](https://s3.amazonaws.com/metro-extracts.mapzen.com/portland_oregon.osm.pbf) and put inside `/var/otp/graphs/pdx` directory

1. Run `java -Xmx2G -jar .[Project-base]/lib/otp-1.1.0-shaded.jar --build /var/otp/graphs/pdx` to create `Graph.obj`

Run: `sbt run`

