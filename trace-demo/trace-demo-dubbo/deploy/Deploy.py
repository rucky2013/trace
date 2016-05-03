#!/usr/bin/python
# -*- coding: utf-8 -*-
import ConfigParser,paramiko,datetime,os,sys,string,fileinput,socket,traceback

class Deploy:

    @staticmethod
    def getFreePort():
        host=["localhost", '10.25.31.15']
        
        cf = ConfigParser.ConfigParser() 
        cf.read("./deploy.properties")
        s = cf.sections() 
        connParas = cf.items('portRange')
        minPort = cf.get("portRange", "minPort")
        maxPort = cf.get("portRange", "maxPort")
        for h in host:
            for port in range(int(minPort), int(maxPort)):
                try:
                    s=socket.socket(socket.AF_INET,socket.SOCK_STREAM)
                    s.bind((h,port))
                    s.close()
                    print (port , 'is free')
                    return port
                except:
                    exstr = traceback.format_exc()
                    print (port , 'is busy')

    @staticmethod
    def upload():
        cf = ConfigParser.ConfigParser() 
        cf.read("./deploy.properties") 
        s = cf.sections() 
        connParas = cf.items('connParas')
        ip = cf.get("connParas", "ip")
        username = cf.get("connParas", "username")
        password = cf.get("connParas", "password")
        port=cf.get("connParas","port")
        #本地路径
        l_dir=cf.get("connParas", "l_dir")
        #远程路径
        r_dir=cf.get("connParas", "r_dir")
            
        #打印分割符
        print('    Server : '+ip)

        #建立连接
        t=paramiko.Transport((ip,int(port)))
        t.connect(username=username,password=password)
        sftp=paramiko.SFTPClient.from_transport(t)
    
        #分别上传文件
        files=os.listdir(l_dir)
        for f in files:
            #本地路径+文件名
            l_file=os.path.join(l_dir,f)
            sufix = l_file[l_file.rfind('.'):]
            if('.war' != sufix):
                continue
            #远程路径+文件名
            r_file=os.path.join(r_dir,f)
            print('        '+l_file+' ---> '+r_file)
            #上传
            sftp.put(l_file,r_file)
            break
        t.close() 
        
    @staticmethod
    def exeScript():
        cf = ConfigParser.ConfigParser() 
        cf.read("./deploy.properties") 
        s = cf.sections() 
        connParas = cf.items('connParas')
        ip = cf.get("connParas", "ip")
        username = cf.get("connParas", "username")
        password = cf.get("connParas", "password")

        #建立连接
        s=paramiko.SSHClient()
        s.set_missing_host_key_policy(paramiko.AutoAddPolicy())
        s.connect(hostname = ip,username = username, password = password)

        cmdList = cf.items('cmdList')
        for commond in cmdList:
            cmd = commond[1].replace('argv1',sys.argv[1])
            cmd = cmd.replace('argv2',sys.argv[2])
            cmd = cmd.replace('argv3',sys.argv[3])
            stdin,stdout,stderr=s.exec_command(cmd)
            print (stdout.read())
        #关闭连接
        s.close()

#Deploy.getFreePort()
Deploy.upload()
Deploy.exeScript()
