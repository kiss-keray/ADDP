import * as React from 'react';
import './App.css';
import { Input, Tabs } from "antd";
const Search = Input.Search;
import { connect, ConnectedComponentClass } from 'react-redux';
import { withRouter } from 'react-router-dom';
import { serviceSearch } from './rpc-redux/actions';
const searchSelectStyle = {
    margin: '0 5% 30px 5%',
    width: 'auto'
}
const TabPane = Tabs.TabPane;
class SearchInput extends React.Component<any, any>{
    public state = {
        searchSelect: "service",
        searchDesc: '服务格式 com.xxx.xxx.Service:1.0.0'
    }
    public constructor(props: any) {
        super(props);
        this.selectSearchClick = this.selectSearchClick.bind(this);
        this.searchFetch = this.searchFetch.bind(this);
    }
    public setState(state: any) {
        super.setState(state, () => window['AddpContext']['App'] = this.state);
    }
    // 服务搜索按钮类型点击
    public selectSearchClick = (searchSelect: string, searchDesc?: string) => () => {
        this.setState({ searchSelect, searchDesc })
    };
    // 服务搜索
    public searchFetch = (searchInput: string) => {
        this.props.history.push('/index');
        this.props.dispatch(serviceSearch(this.state.searchSelect, searchInput));
    };
    public render() {
        // console.log("SearchInput start...");
        return (
            <div style={{ padding: '20px 5%' }}>
                <Tabs onChange={this.selectSearchClick} type="card"
                    style={{ backgroundColor: '#fff' }} tabBarGutter={0}
                    tabBarStyle={{
                        backgroundColor:'rgb(240,242,245)'
                    }}>
                    <TabPane tab='服务' key="service" style={searchSelectStyle} className={'itab'}>
                        <Search
                            placeholder='服务格式 com.xxx.xxx.Service:1.0.0'
                            enterButton="Search"
                            size="large"
                            onSearch={key => this.searchFetch(key)}
                        />
                    </TabPane>
                    <TabPane tab='IP' key="ip" style={searchSelectStyle} className='itab'>
                        <Search
                            placeholder='IP 192.168.0.1'
                            enterButton="Search"
                            size="large"
                            onSearch={key => this.searchFetch(key)}
                        />
                    </TabPane>
                    <TabPane tab='应用' key="app" style={searchSelectStyle} className='itab'>
                        <Search
                            placeholder='应用名 app'
                            enterButton="Search"
                            size="large"
                            onSearch={key => this.searchFetch(key)}
                        />
                    </TabPane>
                </Tabs>
                {/* <div>
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
                        onSearch={key => this.searchFetch(key)}
                    />
                </div> */}
            </div>
        );
    }
}
export default withRouter((connect(
    () => {
        return {}
    },
    (dispatch) => ({
        dispatch: (func: any) => func(dispatch)
    })
)(SearchInput) as ConnectedComponentClass<typeof SearchInput, any>));