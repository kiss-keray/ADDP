# -*- coding: utf-8 -*-
import tornado
import tornado.websocket
import paramiko
import threading
import time
import sys
import json
import requests
import certifi
# 配置服务器信息
PORT = 22
THREAD_COUNT = 0
class MyThread(threading.Thread):
    def __init__(self, id, chan):
        threading.Thread.__init__(self)
        self.chan = chan
        print("new thread=",id)

    def run(self):
        while not self.chan.chan.exit_status_ready():
            time.sleep(0.1)
            try:
                data = self.chan.chan.recv(1024)
                self.chan.write_message(data)
            except Exception as ex:
                pass
        self.chan.sshclient.close()
        return False


def httpGet(id):
    res = requests.get("http://ssh.xx11.top/ssh/shell/get/" + id,verify=False)
    return res.text

class webSSHServer(tornado.websocket.WebSocketHandler):
    def __init__(self, application, request, **kwargs):
        tornado.websocket.WebSocketHandler.__init__(self, application, request, **kwargs)
        print("init")
    def open(self):
        print("open")
    
    def on_message(self, message):
        try:
            try:
                data = json.loads(message);
                if(data['key'] == "connect"):
                    result = httpGet(data['id'])
                    data = json.loads(result)
                    if self.connectSSH(data['ip'],data['username'],data['password']) == False:
                        self.on_close()
            except Exception as ex:
                self.chan.send(message)
        except Exception as ex:
            print("connect execption",ex)

    def on_close(self):
        print("coles",self.sshclient)
        self.sshclient.close()

    def check_origin(self, origin):
        # 允许跨域访问
        return True
    def connectSSH(self,ip,username,password):
        try:
            self.sshclient = paramiko.SSHClient()
            print("new sshclient:",self.sshclient)
            self.sshclient.load_system_host_keys()
            self.sshclient.set_missing_host_key_policy(paramiko.AutoAddPolicy())
            self.sshclient.connect(ip, PORT, username, password,timeout=600.0,banner_timeout=10.0,auth_timeout=10.0)
            self.chan = self.sshclient.invoke_shell(term='xterm',width=120,height=50)
            self.chan.settimeout(0)
            global THREAD_COUNT
            t1 = MyThread(THREAD_COUNT,self)
            THREAD_COUNT += 1
            t1.setDaemon(True)
            t1.start()
            return True
        except Exception as ex:
            print("root execption",ex)
            return False

if __name__ == '__main__':
    # 定义路由
    app = tornado.web.Application([
        (r"/ws/terminals/", webSSHServer),
    ],
        debug=True
    )

    # 启动服务器
    http_server = tornado.httpserver.HTTPServer(app)
    http_server.listen(8900)
    print("server start....")
    tornado.ioloop.IOLoop.current().start()
