#!/usr/bin/env python3

import io
import os

mapledir='../src/main/java/org/snlab/maple'

mynum = 0;
mycode = 0;
othernum = 0;
othercode = 0;

for mydir,mysubdirs,myfiles in os.walk(mapledir):
	assert isinstance(mydir,str)
	if not mydir.startswith(mapledir+'/packet/'):
		for f in myfiles:
			fp = open(mydir+"/"+f)
			lines = fp.readlines()
			mynum = mynum+len(lines)
			lines =[item for item in filter(lambda x:str(x).strip()!="",lines)]
			print("%-20s lines= %d"%(f,len(lines)))
			mycode = mycode + len(lines)
	else:
		for f in myfiles:
			fp = open(mydir+"/"+f)
			lines = fp.readlines()
			othernum = othernum + len(lines)
			lines =[item for item in filter(lambda x:str(x).strip()!="",lines)]
			othercode = othercode + len(lines)

print("mycode=%d, mynum=%d, othercode=%d, othernum=%d"%(mycode, mynum, othercode, othernum))
