import React, { Component } from 'react';
import { Terminal } from "xterm";
import './xterm.css';
import * as fit from "xterm/lib/addons/fit/fit";

Terminal.applyAddon(fit);
class ShellBox extends Component{
    constructor(props) {
        super(props);
        this.state = {
            socket:null,
            xterm:null,
            loginStatus:null
        }
    }
    static loginStatus =  null;
    onmessage = (event) => {

    };
    componentDidMount = () => {
        let xterm = new Terminal({
            rows:50
        });
        xterm._initialized = true;
        xterm.open(document.getElementById("shell-container"));
        let socket = new WebSocket(`ws://ssh.xx11.top/ws/terminals/`);
        socket.onopen = ev => {
            console.log("websocket open",ev);
        };
        socket.onmessage = ev => {
            xterm.write(ev.data);
            if (this.state.loginStatus == null) {
                this.setState({
                    socket:this.state.socket,
                    xterm:this.state.xterm,
                    loginStatus:true
                })
            }
        };
        socket.onclose = ev => {
            console.log("websocket onclose",ev);
            if (this.state.loginStatus == null) {
                this.setState({
                    socket:this.state.socket,
                    xterm:this.state.xterm,
                    loginStatus:false
                })
            }
        };
        socket.onerror = ev => {
            console.log("websocket onerror",ev);
        };
        xterm.on('data',function(data){
            socket.send(data.toString())
        });
        this.setState({
                socket:socket,
                xterm:xterm
            })
    };
    write(data){
        this.state.xterm.write(data);
    }
    render() {
        return (<p/>)
    };
}

export default ShellBox;