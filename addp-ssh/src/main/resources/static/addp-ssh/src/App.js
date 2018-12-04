import React, { Component } from 'react';
import './App.css';
import Websocket from 'react-websocket';
class App extends Component {
    constructor(props) {
        super(props);
        this.websocket = React.createRef();
        this.state = {
            shellData:[<p>react shell</p>]
        };
    }
    static login = false;
    static socketId = "";
    handelChange =  (e) => {
        if ('Enter' === e.key) {
            if (!this.login && 'login' === e.target.value) {
                this.websocket.current.state.ws.send('{"key":"socket_create","ip":"59.110.234.213","username":"root","password":"Kiss4400"}');
                this.login = true;
            } else {
                this.websocket.current.state.ws.send(`{"key":"data","id":"${this.socketId}","command":"${e.target.value}"}`)
            }
            e.target.value = "";
        }
    };
    handleData = (data) => {
        console.log("return:",data)
        data = JSON.parse(data);
        if (data.type === 'init') {
            this.socketId = data.data;
        }else if (data.type === 'shell_result') {
            let old = this.state.shellData;
            old.push(<p>{data.data}</p>);
            this.setState({
                shellData: old
            });
        }
    };
    render() {
      return (
        <div className="shell-box">
            <Websocket url='ws://127.0.0.1:8000/shell/socket' onMessage={this.handleData.bind(this)} ref={this.websocket} />
            <p className="shell-line">{this.state.shellData}</p>
            <input className="shell-input" onKeyDown={this.handelChange.bind(this)} />
            <div className={"full"}></div>
        </div>
      );
    };
}
export default App;
