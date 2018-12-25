import * as React from 'react';
import './App.css';
import { Layout, Menu, Icon, Input, Button,Table } from "antd";
import 'antd/dist/antd.css';
import {Fetch} from './Fetch'
const {
  Header, Content, Footer, Sider
} = Layout;
import IHeader from "./IHeader"
const Search = Input.Search;
interface ITableType {
  service:string,
  group:string,
  app:string
}
function SearchSelectButton(props: any) {
  const changeBackageColor = () => {
    return props.code === props.select;
  }
  return <Button
    style={{
      border: 'none',
      borderRadius: '10px 10px 0 0',
      backgroundColor: changeBackageColor() ? '#fff' : 'rgb(240, 242, 245)',
      minWidth: '100px'
    }}
    onClick={props.clickFun(props.code,props.searchDesc)}
  >{props.desc}</Button>
}
class App extends React.Component {
  public state = {
    collapsed: false,
    searchSelect: "service",
    searchDesc: '服务格式 com.xxx.xxx.Service:1.0.0',
    serviceDate:[{
      service:'com.xxx.xx.Service',
      group:'RPC',
      app:'app'
    }]
  }
  constructor(args: any) {
    super(args);
    this.selectSearchClick = this.selectSearchClick.bind(this);
  }
  public selectSearchClick = (searchSelect: string, searchDesc: string) => () => {
    this.setState({ searchSelect, searchDesc })
  };
  public onCollapse = (collapsed: any) => {
    this.setState({ collapsed });
  };
  public searchFetch = (searchInput:string) => {
    Fetch('/a',{
      method:'POST',
      data:{
        key:searchInput
      }
    }).then(result => {
      console.log("success",result);
      result.json();
    }).catch(error => {
      console.log("error",error);
    })
  };
  public render() {
    return (
      <Layout>
        <Sider
          collapsible={true}
          collapsed={this.state.collapsed}
          onCollapse={this.onCollapse}
          style={{ overflow: 'auto', height: '100vh', left: 0 }}
        >
          <div className="logo" />
          <Menu theme="dark" mode="inline" defaultSelectedKeys={['4']}>
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
          </Menu>
        </Sider>
        <Layout style={{ maxHeight: '100vh' }}>
          <Header style={{ background: '#fff', paddingRight: '4%' }} >
            <IHeader />
          </Header>
          <Content style={{ overflow: 'initial', overflowY: 'hidden' }}>
            <div style={{ padding: '20px 5%' }}>
              <div>
                <SearchSelectButton desc='服务' code='service'
                  select={this.state.searchSelect}
                  clickFun={this.selectSearchClick}
                  searchDesc='服务格式 com.xxx.xxx.Service:1.0.0' />
                <SearchSelectButton desc='IP' code='ip'
                  select={this.state.searchSelect}
                  clickFun={this.selectSearchClick}
                  searchDesc='IP 192.168.0.1' />
                <SearchSelectButton desc='应用' code='app'
                  select={this.state.searchSelect}
                  clickFun={this.selectSearchClick}
                  searchDesc='应用名 app' />
              </div>
              <div style={{ padding: "20px 4%", backgroundColor: '#fff' }}>
                <Search
                  placeholder={this.state.searchDesc}
                  enterButton="Search"
                  size="large"
                  onSearch={this.searchFetch}
                />
              </div>
            </div>
            <div style={{margin: '20px 5%',backgroundColor:'#fff'}}>
              <h4 style={{padding:'10px 1%'}}>查找到的RPC服务10条数据</h4>
              <hr/>
              <Table<ITableType> dataSource={this.state.serviceDate} pagination={{ pageSize: 50 }} scroll={{ y: 240 }} >
                <Table.Column<ITableType> key="service" title="服务ID" dataIndex="service" width={150}/>
                <Table.Column<ITableType> key="group" title="分组" dataIndex="group" width={150}/>
                <Table.Column<ITableType> key="app" title="应用名" dataIndex="app" width={150}/>
                <Table.Column key="操作" title="操作" render = {this.createOperationColumn} width={150}/>
              </Table>
            </div>
          </Content>
          <Footer style={{ textAlign: 'center' }}>
            Ant Design ©2018 Created by Ant UED
      </Footer>
        </Layout>
      </Layout>
    );
  }
  private createOperationColumn(){
    return <div style={{minWidth:'200px'}}>
      <Button type="primary">详情</Button>
      <Button type="primary">测试</Button>
    </div>;
  }
}

export default App;
