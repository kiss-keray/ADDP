import * as React from 'react';
import './App.css';
import { Layout, Menu, Icon, Input, Button, Table, message } from "antd";
import 'antd/dist/antd.css';
import { Fetch } from './Fetch'
const {
  Header, Content, Footer, Sider
} = Layout;
import IHeader from "./IHeader"
const Search = Input.Search;
interface ITableType {
  service: string,
  group: string,
  appName: string
}
interface IServiceTableType {
  ip: string,
  port: number,
}
const searchSelectStyle = {
  border: 'none',
  borderRadius: '10px 10px 0 0',
  minWidth: '100px'
}
function SearchSelectButton(props: any) {
  const changeBackageColor = () => {
    return props.code === props.select;
  }
  const getStyle = (): any => {
    let style: any = {};
    for (let key in props.pubStyle) {
      if (props.pubStyle.hasOwnProperty(key)) {
        style[key] = props.pubStyle[key];
      }
    }
    if (changeBackageColor() && props.selectStyle) {
      for (let key in props.selectStyle) {
        if (props.selectStyle.hasOwnProperty(key)) {
          style[key] = props.selectStyle[key];
        }
      }
    } else if (props.unSelectStyle) {
      for (let key in props.unSelectStyle) {
        if (props.unSelectStyle.hasOwnProperty(key)) {
          style[key] = props.unSelectStyle[key];
        }
      }
    }
    return style;
  }
  return <Button
    style={getStyle()}
    onClick={props.clickFun(props.code, props.searchDesc)}
  >{props.desc}</Button>
}
class App extends React.Component {
  public state = {
    collapsed: false,
    searchSelect: "service",
    searchDesc: '服务格式 com.xxx.xxx.Service:1.0.0',
    serviceData: [{
      service: 'com.nix.jingxun.addp.rpc.producer.test.Hello',
      group: 'RPC',
      appName: 'app1',
      key: 'com.nix.jingxun.addp.rpc.producer.test.Hello-app1-RPC-1.0.0'
    }],
    contentType: 'index',
    detailSelectType: 'producer',
    serviceDetailTable: [{
      ip: '192.168.0.1',
      port: 15000
    }]
  }
  constructor(args: any) {
    super(args);
    this.selectSearchClick = this.selectSearchClick.bind(this);
    this.serviceDetail = this.serviceDetail.bind(this);
    this.selectDetailType = this.selectDetailType.bind(this);
    window['AddpContext'] = this.state;
  }
  // 服务搜索按钮点击
  public selectSearchClick = (searchSelect: string, searchDesc: string) => () => {
    this.setState({ searchSelect, searchDesc })
  };
  // 详情界面纬度选择按钮时间
  public selectDetailType = (detailSelectType: 'producer' | 'consumer') => () => {
    this.setState({ detailSelectType });
  }

  public onCollapse = (collapsed: any) => {
    this.setState({ collapsed });
  };
  // 服务搜索
  public searchFetch = (searchInput: string) => {
    Fetch(`/ops/search/${this.state.searchSelect}?key=${searchInput}`, {
      method: 'GET'
    }).then((serviceData: any[]) => {
      serviceData.map(item => item.service = item.interfaceName);
      console.log('data = ', serviceData);
      this.setState({ serviceData })
    }).catch(error => {
      console.log("error", error);
      message.error('查询服务列表失败！！！');
    })
  };
  // 测试获取服务详情
  public serviceDetail = (sign: string) => {
    Fetch(`/ops/detail/?sign=${sign}`, {
      method: 'GET'
    }).then((detail: any) => {
      console.log('data = ', detail);
    }).catch(error => {
      console.log("error", error);
      message.error('获取服务详情失败！！！');
    });
  }
  // 获取服务所有的服务提供方
  public getServiceProducers = (sign: string): void => {
    Fetch(`/ops/producers/?sign=${sign}`, {
      method: 'GET'
    }).then((serviceDetailTable: any[]) => {
      serviceDetailTable = serviceDetailTable.map(item => {
        return {
          ip: item.split(':')[0],
          port: Number.parseInt(item.split(':')[1],10)
        }
      });
      const contentType = 'detail';
      this.setState({ serviceDetailTable, contentType })
    }).catch(error => {
      console.log("error", error);
      message.error('获取服务详情失败！！！');
    });
  }

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
                  pubStyle={searchSelectStyle}
                  selectStyle={{ backgroundColor: '#fff' }}
                  unSelectStyle={{ backgroundColor: 'rgb(240, 242, 245)' }}
                  searchDesc='服务格式 com.xxx.xxx.Service:1.0.0' />
                <SearchSelectButton desc='IP' code='ip'
                  select={this.state.searchSelect}
                  clickFun={this.selectSearchClick}
                  pubStyle={searchSelectStyle}
                  selectStyle={{ backgroundColor: '#fff' }}
                  unSelectStyle={{ backgroundColor: 'rgb(240, 242, 245)' }}
                  searchDesc='IP 192.168.0.1' />
                <SearchSelectButton desc='应用' code='app'
                  select={this.state.searchSelect}
                  clickFun={this.selectSearchClick}
                  pubStyle={searchSelectStyle}
                  selectStyle={{ backgroundColor: '#fff' }}
                  unSelectStyle={{ backgroundColor: 'rgb(240, 242, 245)' }}
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
            {/* index(search) 界面 */}
            <div className='conetntDiv' style={{ display: this.state.contentType === 'index' ? 'block' : 'none' }} >
              <h4>查找到的RPC服务10条数据</h4>
              <hr />
              <Table<ITableType> dataSource={this.state.serviceData} pagination={{ pageSize: 50 }} scroll={{ y: 240 }} >
                <Table.Column<ITableType> key="service" title="服务ID" dataIndex="service" width="30%" />
                <Table.Column<ITableType> key="group" title="分组" dataIndex="group" width="15%" />
                <Table.Column<ITableType> key="appName" title="应用名" dataIndex="appName" width="15%" />
                <Table.Column key="操作" title="操作" render={this.createOperationColumn} width="20%" />
              </Table>
            </div>
            {/* detail 点击服务详情界面 */}
            <div className='conetntDiv' style={{ display: this.state.contentType === 'detail' ? 'block' : 'none' }}>
              <h4>服务器列表</h4>
              <div>
                <SearchSelectButton desc='服务发布者' code='producer'
                  select={this.state.detailSelectType}
                  clickFun={this.selectDetailType}
                  pubStyle={searchSelectStyle}
                  selectStyle={{ borderColor: 'rgb(240, 242, 245)', borderStyle: 'ridge', borderBottomStyle: 'none', borderWidth: 'initial' }}
                  unSelectStyle={{ borderColor: '#fff', borderWidth: 'initial' }}
                  searchDesc='服务发布者' />
                <SearchSelectButton desc='服务消费者' code='consumer'
                  select={this.state.detailSelectType}
                  clickFun={this.selectDetailType}
                  pubStyle={searchSelectStyle}
                  selectStyle={{ borderColor: 'rgb(240, 242, 245)', borderStyle: 'ridge', borderBottomStyle: 'none', borderWidth: 'initial' }}
                  unSelectStyle={{ borderColor: '#fff', borderWidth: 'initial' }}
                  searchDesc='服务消费者' />
              </div>
              <Table<IServiceTableType> dataSource={this.state.serviceDetailTable} pagination={{ pageSize: 50 }} scroll={{ y: 240 }} >
                <Table.Column<IServiceTableType> title="服务方IP" dataIndex="ip" width="70%" />
                <Table.Column<IServiceTableType> title="服务方端口" dataIndex="port" width="30%" />
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
  private createOperationColumn = (rowData: any) => {
    return <div>
      <Button type="primary" onClick={() => this.getServiceProducers(rowData.key)}>详情</Button>
      <Button type="primary" onClick={() => this.serviceDetail(rowData.key)}>测试</Button>
    </div>;
  }
}

export default App;
