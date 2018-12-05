# -*- coding: utf-8 -*-
import tornado
import tornado.websocket
import paramiko
import threading
import time
import sys
import json
import requests

# 配置服务器信息
PORT = 22
class MyThread(threading.Thread):
    def __init__(self, id, chan):
        threading.Thread.__init__(self)
        self.chan = chan

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
    res=requests.get("http://ssh.xx11.top/ssh/shell/get/" + id)
    return res.text

class webSSHServer(tornado.websocket.WebSocketHandler):
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
            print(ex)

    def on_close(self):
        self.sshclient.close()

    def check_origin(self, origin):
        # 允许跨域访问
        return True
    def connectSSH(self,ip,username,password):
        try:
            self.sshclient = paramiko.SSHClient()
            self.sshclient.load_system_host_keys()
            self.sshclient.set_missing_host_key_policy(paramiko.AutoAddPolicy())
            self.sshclient.connect(ip, PORT, username, password)
            self.chan = self.sshclient.invoke_shell(term='xterm')
            self.chan.settimeout(0)
            t1 = MyThread(999, self)
            t1.setDaemon(True)
            t1.start()
            return True
        except Exception as ex:
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
    tornado.ioloop.IOLoop.current().start()
