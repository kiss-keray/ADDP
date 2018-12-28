import * as React from 'react';
import './App.css';
import { Layout } from "antd";
import { HashRouter, Route, Switch, Redirect } from 'react-router-dom';
import 'antd/dist/antd.css';
const { Header, Content, Footer } = Layout;
import IHeader from "./IHeader"
import IndexSearch from './page/indexSearch';
import ServiceDetail from './page/serviceDetail';
import ServiceTest from './page/serviceTest';
import Nav from './page/nav';
import SearchInput from './SearchInout';
const getConfirmation = (msg: string, callback: (ok: boolean) => void): void => {
  const allowTransition = window.confirm(msg);
  callback(allowTransition);
};
class App extends React.Component<any, any> {
  constructor(props: any) {
    super(props);
    window['AddpContext']['App'] = {};
  }
  public render() {
    return (
      <HashRouter basename="/" getUserConfirmation={getConfirmation}>
        <Layout>
          <Nav />
          <Layout style={{ maxHeight: '100vh' }}>
            <Header style={{ background: '#fff', paddingRight: '4%' }} >
              <IHeader />
            </Header>
            <Content style={{ overflow: 'initial', overflowY: 'hidden' }}>
              <SearchInput />
              <Switch>
                <Redirect exact={true} from="/" to="/index" />
                {[
                  <Route path="/index" key='index' component={IndexSearch} />,
                  <Route path="/detail" key='detail' component={ServiceDetail} />,
                  <Route path="/serviceTest" key='serviceTest' component={ServiceTest} />
                ]}
              </Switch>
            </Content>
            <Footer style={{ textAlign: 'center' }}>
              Ant Design ©2018 Created by Ant UED
            </Footer>
          </Layout>
        </Layout>
      </HashRouter>
    );
  }
}
export default App;
