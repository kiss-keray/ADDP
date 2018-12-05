import React, { Component } from 'react';
import { Terminal } from "xterm";
import './xterm.css';
import * as fit from "xterm/lib/addons/fit/fit";

Terminal.applyAddon(fit);
class ShellBox extends Component{
    static socket;
    static xterm;
    componentDidMount = () => {
        let xterm = new Terminal({
            rows:50
        });
        let socket = new WebSocket("ws://127.0.0.1:8888/terminals/");
        xterm._initialized = true;
        // xterm.attach(socket);
        socket.onopen = ev => {
            console.log("websocket open",ev);
        };
        socket.onmessage = function(event) {
            console.log('on message:',event.data);
            xterm.write(event.data);
        };
        socket.onclose = ev => {
            console.log("websocket onclose",ev);
        };
        socket.onerror = ev => {
            console.log("websocket onerror",ev);
        };
        xterm.on('data',function(data){
            console.log('data xterm=>',data);
            socket.send(data.toString())
        });

        xterm.open(document.getElementById("shell-container"));
        this.socket = socket;
        this.xterm = xterm;

    };
    login(ip,username,passord) {
        this.socket.send(`{"key":"login","ip":"${ip}"},"username":"${username}","password":"${passord}"`)
    }
    render() {
        return (<p/>)
    };
}
export default ShellBox;