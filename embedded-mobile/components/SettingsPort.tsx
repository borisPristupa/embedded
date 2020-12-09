import * as React from 'react';
import {Text, View} from './Themed';
import {Alert, StyleSheet} from 'react-native';

import ModalSelector from 'react-native-modal-selector'


export default function SettingsPort(props: any) {
    const [error, setError] = React.useState(false)

    let index = 0;
    const data = [
        {key: index++, label: '9600'},
        {key: index++, label: '19200'},
        {key: index++, label: '38400'},
        {key: index++, label: '57600'},
        {key: index++, label: '115200'},
    ];

    const onChange = (option: any) => {
        console.log(option)
        fetch(`http://192.168.0.40:8080/manage?port=com${props.number}&speed=${option.label}`)
            .then((response) => {
                setError(false);
                if (response.status !== 200) {
                    setError(true);
                    console.log('Error: ' + response.status)
                    setTimeout(() => createAlert(),1000);
                }
            })
            .catch(() => setError(true))
    }

    return (
        <View style={styles.ch_box}>
            <Text style={styles.port}>Port #{props.number}</Text>
            <ModalSelector
                data={data}
                initValue="Speed!"
                animationType={"slide"}
                selectTextStyle={styles.text}
                selectStyle={styles.selectModel}
                touchableStyle={styles.touch}
                backdropPressToClose={true}
                cancelStyle={{display: 'none'}}
                onChange={onChange}
            />
        </View>
    );
}

const createAlert = () => {
    console.log('createAlert')
    Alert.alert(
        "🙁 Failed to change port speed",
        "Please try again later",
        [{text: "OK", onPress: () => console.log("OK Pressed")}],
        {cancelable: false}
    );
}


const styles = StyleSheet.create({
    ch_box: {
        width: '80%',
        // backgroundColor: '#E5E8F0',
        backgroundColor: 'rgba(229,232,240, 0.5)',
        margin: 10,
        borderRadius: 5,
        alignSelf:'center',
    },
    selectModel: {
        backgroundColor: '#2B2E4A',
        borderRadius: 10,
        width: '100%',
        alignSelf: 'center',
    },
    text: {
        color: 'white',
    },
    touch: {
        width: '50%',
        alignSelf: 'center',
    },
    image: {
        flex: 1,
        resizeMode: "cover",
        justifyContent: "center"
    },
    port: {
        width: '50%',
        fontSize: 18,
        fontWeight: 'bold',
        color: '#FFF',
    },
});

