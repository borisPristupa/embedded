import * as React from 'react';
import {StyleSheet, ImageBackground} from 'react-native';
import SettingsPort from '../components/SettingsPort';
import {Text} from '../components/Themed';

export default function ConfigScreen() {
    return (
        <ImageBackground source={require('../assets/images/default.jpg')} style={styles.container}>
            <Text style={styles.title}>Hello</Text>
            <SettingsPort number='1'/>
            <SettingsPort number='2'/>
        </ImageBackground>
    );
}

const styles = StyleSheet.create({
    container: {
        alignItems: 'center',
        justifyContent: 'center',
        width: "100%",
        flex: 1,
        backgroundColor: "#8bc34a"
    },
    title: {
        fontSize: 20,
        marginVertical: 3,
        fontWeight: 'bold',
        alignSelf: "center",
        color: "white",
    },
});
