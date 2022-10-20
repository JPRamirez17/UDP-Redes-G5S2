#!/bin/sh
sudo wget http://ipv4.download.thinkbroadband.com/100MB.zip
sudo wget http://ipv4.download.thinkbroadband.com/200MB.zip
sudo wget http://ipv4.download.thinkbroadband.com/50MB.zip
sudo mv 100MB.zip 100MB.txt
sudo mv 200MB.zip 200MB.txt
sudo mv 50MB.zip 50MB.txt
sudo cat 200MB.txt 50MB.txt > 250MB.txt
sudo rm 200MB.txt 50MB.txt
