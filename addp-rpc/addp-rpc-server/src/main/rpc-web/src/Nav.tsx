import * as React from 'react';
import { withRouter } from 'react-router-dom';
import { Layout, Menu, Icon } from "antd";
const { Sider } = Layout;
class Nav extends React.Component<any, any> {
  public state = {
    collapsed: false,
    selectKey:'index_search'
  }
  constructor(props: any) {
    super(props);
    window['AddpContext']['Nav'] = {};
    this.setNav = this.setNav.bind(this);
    window['navFun'] = this.setNav;
  }
  public setNav = (selectKey:string) => {
    this.setState({selectKey});
  }
  public setState(state: any) {
    super.setState(state,() => window['AddpContext']['Nav'] = this.state);
  }
  public onCollapse = (collapsed: any) => {
    this.setState({ collapsed });
  };

  // 点击导航
  public clickNav = (nav: string) => () => {
    this.props.history.push(nav);
  };
  public render() {
    return (
      <Sider
        collapsible={true}
        collapsed={this.state.collapsed}
        onCollapse={this.onCollapse}
        style={{ overflow: 'auto', height: '100vh', left: 0 }}
      >
        <div className="logo" />
        <Menu theme="dark" mode="inline" selectedKeys={[this.state.selectKey]} >
          <Menu.Item key="index_search" onClick={this.clickNav('/index')}>
            <Icon type="user" />
            <span className="nav-text">服务搜索</span>
          </Menu.Item>
          <Menu.Item key="service_detail" onClick={this.clickNav('/detail')}>
            <Icon type="video-camera" />
            <span className="nav-text">服务详情</span>
          </Menu.Item>
          <Menu.Item key="service_test">
            <Icon type="upload" />
            <span className="nav-text">服务测试</span>
          </Menu.Item>
        </Menu>
      </Sider>
    );
  }
}
export default withRouter(Nav);