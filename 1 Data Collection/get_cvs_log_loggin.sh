#!/usr/bin/expect -f

set server [lindex $argv 0]
spawn cvs -d $server login;
expect "CVS password: "
send "\r"
expect eof

