import * as React from 'react';
import './App.css';
import { Layout, Input, message } from "antd";
import { MemoryRouter, Route, Switch, Redirect } from 'react-router-dom';
import 'antd/dist/antd.css';
import Fetch from './Fetch'
import { SearchSelectButton } from "./MyComponent"
const { Header, Content, Footer } = Layout;
import IHeader from "./IHeader"
import IndexSearch from './IndexSearch';
import ServiceDetail from './ServiceDetail';
import Nav from './Nav';
const Search = Input.Search;
const searchSelectStyle = {
  border: 'none',
  borderRadius: '10px 10px 0 0',
  minWidth: '100px'
}
const initialRouterEntries = [
  '/',
  {
    pathname: '/index'
  },
  {
    pathname: '/detail'
  }
];
const getConfirmation = (msg: string, callback: (ok: boolean) => void): void => {
  const allowTransition = window.confirm(msg);
  callback(allowTransition);
};
class App extends React.Component<any, any> {
  public state = {
    searchSelect: "service",
    searchDesc: '服务格式 com.xxx.xxx.Service:1.0.0'
  }
  constructor(props: any) {
    super(props);
    window['AddpContext']['App'] = {};
    this.selectSearchClick = this.selectSearchClick.bind(this);
  }
  public setState(state: any) {
    super.setState(state,() => window['AddpContext']['App'] = this.state);
  }
  // 服务搜索按钮点击
  public selectSearchClick = (searchSelect: string, searchDesc: string) => () => {
    this.setState({ searchSelect, searchDesc })
  };
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

  public render() {
    return (
      <MemoryRouter initialEntries={initialRouterEntries} initialIndex={1} getUserConfirmation={getConfirmation}>
        <Layout>
          <Nav/>
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
              <Switch>
                <Redirect exact={true} from="/" to="/index" />
                {[
                  <Route path="/index" key='index' component={IndexSearch} />,
                  <Route path="/detail" key='detail' component={ServiceDetail} />,
                  <Route path="/test" key='test' component={ServiceDetail} />
                ]}
              </Switch>
            </Content>
            <Footer style={{ textAlign: 'center' }}>
              Ant Design ©2018 Created by Ant UED
            </Footer>
          </Layout>
        </Layout>
      </MemoryRouter>
    );
  }
}

export default App;
