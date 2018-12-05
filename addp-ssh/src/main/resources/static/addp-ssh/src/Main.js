import React, { Component } from 'react';
import './Main.css';
import ShellBox from "./ShellBox"
class Main extends Component {
    constructor(props) {
        super(props);
    }

    render() {
      return (
          <div >
              <ShellBox/>
              ip:<input className={"main-input"}/>username<input className={"main-input"}/>password<input className={"main-input"}/><input type="submit" value={"登陆"}/>
          </div>
      );
    };
}
export default Main;
