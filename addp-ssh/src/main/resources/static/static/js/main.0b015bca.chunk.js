(window.webpackJsonp=window.webpackJsonp||[]).push([[0],{104:function(e,t,n){},144:function(e,t,n){e.exports=n(304)},182:function(e,t,n){},304:function(e,t,n){"use strict";n.r(t);var a=n(0),o=n.n(a),r=n(9),l=n.n(r),s=n(36),c=n(37),i=n(39),u=n(38),m=n(40),d=(n(96),n(91)),p=(n(182),n(104),n(122)),f=n(310),g=n(307),h=(n(105),n(123)),b=n.n(h),S=n(306),w=n(305),k=n(308),v=n(15),y=n(309),E=n(17),x=S.a.Item,O=function(e){function t(){var e,n;Object(s.a)(this,t);for(var a=arguments.length,o=new Array(a),r=0;r<a;r++)o[r]=arguments[r];return(n=Object(i.a)(this,(e=Object(u.a)(t)).call.apply(e,[this].concat(o)))).handleSubmit=function(e){console.log("props",n.props),e.preventDefault(),console.log("data",n.props.form),n.props.form.validateFields(function(e,t){e||n.props.onClick(t)})},n}return Object(m.a)(t,e),Object(c.a)(t,[{key:"render",value:function(){var e=this.props.form.getFieldDecorator;return o.a.createElement(S.a,{onSubmit:this.handleSubmit,className:"login-form"},o.a.createElement(x,null,e("ip",{rules:[{required:!0,message:"ip\u4e0d\u80fd\u4e3a\u7a7a"}]})(o.a.createElement(k.a,{prefix:o.a.createElement(v.a,{type:"global",style:{color:"rgba(0,0,0,.25)"}}),placeholder:"ip"}))),o.a.createElement(x,null,e("username",{rules:[{required:!0,message:"username\u4e0d\u80fd\u4e3a\u7a7a"}]})(o.a.createElement(k.a,{prefix:o.a.createElement(v.a,{type:"user",style:{color:"rgba(0,0,0,.25)"}}),placeholder:"username"}))),o.a.createElement(x,null,e("password",{rules:[{required:!0,message:"password\u4e0d\u80fd\u4e3a\u7a7a"}]})(o.a.createElement(k.a,{prefix:o.a.createElement(v.a,{type:"lock",style:{color:"rgba(0,0,0,.25)"}}),type:"password",placeholder:"password"}))),o.a.createElement(x,null,e("remember",{valuePropName:"checked",initialValue:!0})(o.a.createElement(y.a,null,"Remember me")),o.a.createElement(E.a,{type:"primary",htmlType:"submit",className:"login-form-button"},"\u8fde\u63a5")))}}]),t}(o.a.Component),j=S.a.create()(O);S.a.Item;d.Terminal.applyAddon(p);var I=function(e){function t(e){var n;return Object(s.a)(this,t),(n=Object(i.a)(this,Object(u.a)(t).call(this,e))).componentDidMount=function(){n.createXterm()},n.createWebsocket=function(e){var t=new WebSocket("ws://59.110.234.213:8900/ws/terminals/");t.onopen=function(t){console.log("websocket open",t),e(),n.state.xterm.clear()},t.onmessage=function(e){n.state.xterm.write(e.data),null==n.state.loginStatus&&(f.a.success("SSH \u8fde\u63a5\u6210\u529f",1),n.setState({socket:n.state.socket,xterm:n.state.xterm,loginStatus:!0,loading:!1}))},t.onclose=function(e){console.log("websocket onclose",e),null==n.state.loginStatus?(f.a.error("SSH \u8fde\u63a5\u5931\u8d25\uff01\uff01\uff01",5),n.setState({socket:n.state.socket,xterm:n.state.xterm,loginStatus:!1,loading:!1,modalStatus:!0})):g.a.warning({title:"SSH\u8fde\u63a5\u65ad\u5f00",content:"\u8fde\u63a5\u5df2\u7ecf\u5173\u95ed\uff0c\u8bf7\u91cd\u65b0\u8fde\u63a5",onOk:function(){n.setState({loginStatus:null,loading:!1,modalStatus:!0})}})},t.onerror=function(e){f.a.error("SSH shell \u5931\u8d25",2)},n.setState({socket:t})},n.createXterm=function(){var e=new d.Terminal({rows:50,cols:120});e._initialized=!0,e.open(document.getElementById("shell-container")),e.on("data",n.xtermData),n.setState({xterm:e})},n.xtermData=function(e){console.log("state",n.state),n.state.socket.send(e.toString())},n.loginClick=function(e){n.createWebsocket(function(){b.a.create({url:"/ssh/shell/create",method:"POST",data:e,fit:function(e){return{success:!0,content:e}},process:function(e){return e}})().then(function(e){console.log("result",e),n.state.socket.send('{"key":"connect","id":"'.concat(e,'"}'))}).catch(function(e){console.log("error",e);n.setState({loading:!1})});n.setState({modalStatus:!1,loading:!0})})},n.ipInput=function(e){n.ip=e.target.value},n.usernameInput=function(e){n.username=e.target.value},n.passwordInput=function(e){n.password=e.target.value},n.state={socket:null,xterm:null,loginStatus:null,modalStatus:!0,loading:!1},n}return Object(m.a)(t,e),Object(c.a)(t,[{key:"write",value:function(e){this.state.xterm.write(e)}},{key:"render",value:function(){return o.a.createElement("div",{className:"shell-div",style:{display:this.state.loading?"block":"none"}},o.a.createElement(g.a,{title:"WEB SSH Login",centered:!0,visible:this.state.modalStatus,width:400,footer:null},o.a.createElement(j,{onClick:this.loginClick.bind(this)})),o.a.createElement(w.a,{tip:"SSH \u8fde\u63a5\u4e2d\xb7\xb7\xb7",spinning:this.state.loading,size:"large",style:{position:"absolute",zIndex:"99",top:"45%",left:"45%",width:"10%",height:"10%"}}))}}]),t}(a.Component),C=function(e){function t(e){var n;return Object(s.a)(this,t),(n=Object(i.a)(this,Object(u.a)(t).call(this,e))).websocket=o.a.createRef(),n.shellBox=o.a.createRef(),n}return Object(m.a)(t,e),Object(c.a)(t,[{key:"render",value:function(){return o.a.createElement("div",null,o.a.createElement(I,{ref:this.shellBox}))}}]),t}(a.Component);Boolean("localhost"===window.location.hostname||"[::1]"===window.location.hostname||window.location.hostname.match(/^127(?:\.(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)){3}$/));l.a.render(o.a.createElement(C,null),document.getElementById("root")),"serviceWorker"in navigator&&navigator.serviceWorker.ready.then(function(e){e.unregister()})},96:function(e,t,n){}},[[144,2,1]]]);
//# sourceMappingURL=main.0b015bca.chunk.js.map