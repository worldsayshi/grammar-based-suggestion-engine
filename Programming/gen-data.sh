#!/usr/bin/env bash

cd /vagrant/bundle/Grammar
./build

cd /vagrant/bundle/mock-data/
mvn compile

# debug args
#export MAVEN_OPTS='-Djava.library.path=/usr/local/lib -Xmx1500m -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=1044 -Dgrammar.dir=/vagrant/bundle/Grammar'

export MAVEN_OPTS='-Djava.library.path=/usr/local/lib -Xmx1500m -Dgrammar.dir=/vagrant/bundle/Grammar'
export PATH=/home/vagrant/.cabal/bin:$PATH
export CLASSPATH=/vagrant/bundle/Grammar:CLASSPATH
mvn exec:java # -Dexec.mainClass="org.agfjord.graph.Main" -Dexec.args="%classpath" # -Dexec.classpathScope="java"
