import * as React from 'react';
import {StyleSheet} from 'react-native';
import SettingsPort from '../components/SettingsPort';
import {Text, View} from '../components/Themed';

export default function ConfigScreen() {
    return (
        <View style={styles.container}>
            <Text style={styles.title}>Hello</Text>
            <SettingsPort number='1'/>
            <SettingsPort number='2'/>
        </View>
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
        color: "#005b01",
    },
});
