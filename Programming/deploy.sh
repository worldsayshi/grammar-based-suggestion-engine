#!/usr/bin/env bash
set -e

#source /vagrant/reset-index.sh

# Deploy Solr
cd /vagrant/bundle/solr-mvn/
mvn tomcat7:deploy

# Compile grammar into GF portable grammar format usable from Java
cd /vagrant/bundle/Grammar
./build

# Deploy the user interface
cd /vagrant/bundle/nlparser/
mvn tomcat7:deploy
