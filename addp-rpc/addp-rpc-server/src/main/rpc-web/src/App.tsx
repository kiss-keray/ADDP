import * as React from 'react';
import './App.css';;
import { Layout, Menu, Icon } from "antd";
import 'antd/dist/antd.css';
const {
  Header, Content, Footer, Sider
} = Layout;
class App extends React.Component {
  public state = {
    collapsed: false,
  }

  public onCollapse = (collapsed: any) => {
    console.log(collapsed);
    this.setState({ collapsed });
  };
  public render() {
    return (
      <Layout style={{ minHeight: "100vh" }}>
        <Header style={{ maxHeight: "70px" }} >
          <p>header</p>
        </Header>
        <Content style={{ overflow: "initial" }}>
          <Layout style={{ minHeight: "100vh" }}>
            <Sider
              collapsible={true}
              collapsed={this.state.collapsed}
              onCollapse={this.onCollapse}
            >
              <div className="logo" />
              <Menu theme="dark" mode="inline" defaultSelectedKeys={["4"]}>
                <Menu.Item key="1">
                  <Icon type="user" />
                  <span className="nav-text">nav 1</span>
                </Menu.Item>
                <Menu.Item key="2">
                  <Icon type="video-camera" />
                  <span className="nav-text">nav 2</span>
                </Menu.Item>
                <Menu.Item key="3">
                  <Icon type="upload" />
                  <span className="nav-text">nav 3</span>
                </Menu.Item>
                <Menu.Item key="4">
                  <Icon type="bar-chart" />
                  <span className="nav-text">nav 4</span>
                </Menu.Item>
                <Menu.Item key="5">
                  <Icon type="cloud-o" />
                  <span className="nav-text">nav 5</span>
                </Menu.Item>
                <Menu.Item key="6">
                  <Icon type="appstore-o" />
                  <span className="nav-text">nav 6</span>
                </Menu.Item>
                <Menu.Item key="7">
                  <Icon type="team" />
                  <span className="nav-text">nav 7</span>
                </Menu.Item>
                <Menu.Item key="8">
                  <Icon type="shop" />
                  <span className="nav-text">nav 8</span>
                </Menu.Item>
              </Menu>
            </Sider>
            <Layout style={{ marginLeft: 0 }}>
              <Content style={{ overflow: "initial" }}>
                cen
              </Content>
              <Footer style={{ textAlign: "center" }}>
                Ant Design ©2018 Created by Ant UED
              </Footer>
            </Layout>
          </Layout>
        </Content>
      </Layout>
    );
  }
}

export default App;
