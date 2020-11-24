import * as React from 'react';
import {Text, View} from './Themed';
import {ScrollView, StyleSheet, ImageBackground} from 'react-native';

import ModalSelector from 'react-native-modal-selector'


export default function SettingsPort(props: any) {

    const image = { uri: "https://reactjs.org/logo-og.png" };

    const [speed, setSpeed] = React.useState('9600')
    const [error, setError] = React.useState(false)

    let index = 0;
    const data = [
        { key: index++, label: '9600' },
        { key: index++, label: '19200' },
        { key: index++, label: '38400' },
        { key: index++, label: '57600' },
        { key: index++, label: '115200' },
    ];



    return(
        <View style={styles.ch_box}>
                <Text style={styles.port} >Port #{props.number}</Text>           
                <ModalSelector
                    data={data}
                    initValue="Speed!" 
                    animationType={"slide"}
                    selectTextStyle={styles.text}
                    selectStyle={styles.selectmodelstyle}
                    touchableStyle={styles.touch}
                    backdropPressToClose={true}
                    cancelStyle={{display:'none'}}
                    onChange={(option)=>{ 
                        setSpeed(option.label);
                        fetch(`http://localhost:8080//manage?port=${props.number}&speed=${speed}`)
                            .then(() => setError(false))
                            .catch((responce)=>{
                                setError(true);
                                alert(responce.status);
                                console.log(responce.status + " "+ props.number+" "+option.label);
                            })
                        }}
                /> 
                
                       
        </View>
    );
}


const styles = StyleSheet.create({
    ch_box:{
        width: '95%',
        backgroundColor:'#fff',
        margin: 3,
        borderRadius:5,
        
    },
   selectmodelstyle:{
        backgroundColor:'#2B2E4A',
        borderRadius:10,
        width:'100%',
        alignSelf: 'center',
   },
   text:{
        color: 'white',
   },
   touch:{
    //backgroundColor:'red',
    width: '50%',
    alignSelf:'center',
   },
   image:{
    flex: 1,
    resizeMode: "cover",
    justifyContent: "center"
   },
   port:{
    width:'50%',
    fontSize: 18,
    fontWeight: 'bold',
    color: '#000',
   },
});

