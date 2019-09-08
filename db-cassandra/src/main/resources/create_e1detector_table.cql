CREATE KEYSPACE IF NOT EXISTS sumo_flink
 WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'};
CREATE TABLE IF NOT EXISTS sumo_flink.e1detectorvalue (
	pk uuid PRIMARY KEY,
	start float,
	end float,
	id text,
	nVehContrib int,
	flow float,
	occupancy float,
	speed float,
	harmonicMeanSpeed float,
	length float,
	nVehEntered int
);

