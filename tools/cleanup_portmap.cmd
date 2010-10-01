@echo off
rem restart portmap service

net stop portmap
net start portmap || exit 1