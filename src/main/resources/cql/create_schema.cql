create keyspace if not exists tika WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1' };

use tika; 

create table if not exists metadata (
documentid text PRIMARY KEY,
created_date timestamp,
last_modified timestamp,
version double, 
encrypted boolean,
content_type text,
tags list<text>,
link text, 
type text,
content text,
md_ map<text,text>
);


