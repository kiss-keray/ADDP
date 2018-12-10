import React, { Component } from 'react';
import { Terminal } from "xterm";
import './xterm.css';
import './Main.css';
import './shellBox.css';
import * as fit from "xterm/lib/addons/fit/fit";
import { Modal, Button,Input,message } from 'antd';
import "antd/dist/antd.css";
import nattyFetch from "natty-fetch";
import { Spin, Alert ,Form} from 'antd'
import ShellInput from "./ShellForm"
const FormItem = Form.Item;;
Terminal.applyAddon(fit);
class ShellBox extends Component{
    constructor(props) {
        super(props);
        this.state = {
            socket:null,
            xterm:null,
            loginStatus:null,
            modalStatus:true,
            loading:false
        }
    }
    static ip;
    static username;
    static password;
    componentDidMount = () => {
        this.createXterm();
    };
    write(data){
        this.state.xterm.write(data);
    }
    createWebsocket = (call) => {
        let socket = new WebSocket(`wss://ssh.xx11.top/ws/terminals/`);
        socket.onopen = ev => {
            console.log("websocket open",ev);
            call();
            this.state.xterm.clear();
        };
        socket.onmessage = ev => {
            this.state.xterm.write(ev.data);
            if (this.state.loginStatus == null) {
                message.success('SSH 连接成功',1);
                this.setState({
                    socket:this.state.socket,
                    xterm:this.state.xterm,
                    loginStatus:true,
                    loading:false
                })
            }
        };
        socket.onclose = ev => {
            console.log("websocket onclose",ev);
            if (this.state.loginStatus == null) {
                message.error('SSH 连接失败！！！',5);
                this.setState({
                    socket:this.state.socket,
                    xterm:this.state.xterm,
                    loginStatus:false,
                    loading:false,
                    modalStatus:true
                })
            } else {
                Modal.warning({
                    title: 'SSH连接断开',
                    content: '连接已经关闭，请重新连接',
                    onOk:() => {
                        this.setState({
                            loginStatus:null,
                            loading:false,
                            modalStatus:true
                        })
                    }
                });
            }
        };
        socket.onerror = ev => {
            message.error('SSH shell 失败',2);
        };
        this.setState({socket})
    };
    createXterm = () => {
        let xterm = new Terminal({
            rows:50,
            cols:120
        });
        xterm._initialized = true;
        xterm.open(document.getElementById("shell-container"));
        xterm.on('data',this.xtermData);
        this.setState({xterm})
    };
    xtermData = (data) => {
        console.log("state",this.state);
        this.state.socket.send(data.toString());
    };

    loginClick =  (data) => {
        this.createWebsocket(() => {
            const fetchFoo = nattyFetch.create({
                url: '/ssh/shell/create',
                method:"POST",
                data: data,
                fit: function(response) {
                    // 数据结构适配
                    return {success:true,content:response};
                },
                process: function(content) {
                    // 成功时的数据预处理
                    return content;
                }
            });
            fetchFoo().then(result => {
                console.log("result",result);
                this.state.socket.send(`{"key":"connect","id":"${result}"}`);
            }).catch(error => {
                console.log("error",error)
                let loading = false;
                this.setState({loading})
            });
            let modalStatus = false;
            let loading = true;
            this.setState({modalStatus,loading});
        });
    };
    ipInput = (data) => {
        this.ip = data.target.value;
    };
    usernameInput = (data) => {
        this.username = data.target.value;
    };
    passwordInput = (data) => {
        this.password = data.target.value;
    };
    render() {
        let loadingCss = {
            position:'absolute',
            zIndex:'99',
            top:'45%',
            left:'45%',
            width:'10%',
            height:'10%',
        };
        return (
            <div className="shell-div" style={{display:this.state.loading ? "block" : "none"}}>
                <Modal title="WEB SSH Login" centered
                       visible={this.state.modalStatus}
                       width={400}
                       footer={null}
                >
                    {/*<div className="ssh-input">*/}
                        {/*<Input addonBefore="ssh host" defaultValue="" onChange={this.ipInput.bind(this)}/>*/}
                    {/*</div>*/}
                    {/*<div className="ssh-input">*/}
                        {/*<Input addonBefore="username" defaultValue="" onChange={this.usernameInput.bind(this)}/>*/}
                    {/*</div>*/}
                    {/*<div className="ssh-input">*/}
                        {/*<Input addonBefore="password" defaultValue="" onChange={this.passwordInput.bind(this)}/>*/}
                    {/*</div>*/}
                    <ShellInput onClick={this.loginClick.bind(this)}/>
                </Modal>
                <Spin tip="SSH 连接中···" spinning={this.state.loading} size={"large"} style={loadingCss}>
                </Spin>
            </div>)
    };
}

export default ShellBox;