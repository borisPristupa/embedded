import { preventAutoHide } from 'expo-splash-screen';
import * as React from 'react';
import {StyleSheet, ImageBackground} from 'react-native';
import { ScrollView } from 'react-native-gesture-handler';
import SettingsPort from '../components/SettingsPort';
import {Text} from '../components/Themed';

export default function ConfigScreen() {
    return (
        <ImageBackground source={require('../assets/images/default.jpg')} style={styles.container}>
            <Text style={styles.title}>Hello</Text>
            <ScrollView style={styles.scroll}>
                <SettingsPort number='1'/>
                <SettingsPort number='2'/>
                <SettingsPort number='3'/>
                <SettingsPort number='4'/>
                <SettingsPort number='5'/>
                <SettingsPort number='6'/>
                <SettingsPort number='7'/>
                <SettingsPort number='8'/>
            </ScrollView>
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
    scroll: {
        width: "100%",
        height: "100%",
        alignContent: 'center',
    },
});
