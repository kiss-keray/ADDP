import React, { Component } from 'react';
import './Main.css';
import ShellBox from "./ShellBox"
class Main extends Component {
    constructor(props) {
        super(props);
        this.websocket = React.createRef();
        this.shellBox = React.createRef();
    }

    render() {
      return (
          <div >
              <ShellBox ref={this.shellBox}/>
          </div>
      );
    };
}
export default Main;
