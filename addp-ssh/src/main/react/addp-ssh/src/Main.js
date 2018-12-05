import React, { Component } from 'react';
import './Main.css';
import ShellBox from "./ShellBox"
import nattyFetch from "natty-fetch"
class Main extends Component {
    constructor(props) {
        super(props);
        this.websocket = React.createRef();
        this.shellBox = React.createRef();
    }
    static port = -1;
    static ip;
    static username;
    static password;
    loginClick =  () => {
        const fetchFoo = nattyFetch.create({
            url: '/ssh/shell/create',
            method:"POST",
            data: {
                ip:this.ip.value,
                username:this.username.value,
                password:this.password.value
            },
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
            this.shellBox.current.state.socket.send(`{"key":"connect","id":"${result}"}`);
        }).catch(error => {
            console.log("error",error)
        });
    };

    render() {
      return (
          <div >
              <ShellBox ref={this.shellBox}/>
              ip:<input className={"main-input"} ref={input => this.ip = input}/>
              username:<input className={"main-input"} ref={input => this.username = input}/>
              password:<input className={"main-input"} ref={input => this.password = input}/>
              <input type="submit" onClick={this.loginClick.bind(this)} value={"登陆"} />
          </div>
      );
    };
}
export default Main;
