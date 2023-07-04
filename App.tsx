import {
  Text,
  SafeAreaView,
  Pressable,
  StyleSheet,
  Platform,
} from 'react-native';
import React, { useState } from 'react';
import CustomMethod from './CustomMethod';

const App = () => {
  const [qrCode, setQrCode] = useState<string>('');

  const handlePressScanQR = () => {
    if (Platform.OS === 'android') {
      return CustomMethod?.scanQRCode()
        .then(qrcde => setQrCode(qrcde))
        .catch(e => console.error('Error', e));
    }
    return null;
  };

  return (
    <SafeAreaView>
      <Text style={styles.title}>React Native</Text>

      {qrCode.length > 0 && <Text style={styles.qrResult}>{qrCode}</Text>}

      <Pressable onPress={handlePressScanQR} style={styles.btn}>
        <Text>SCAN QR CODE</Text>
      </Pressable>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  title: {
    fontSize: 20,
    color: 'black',
    marginTop: 20,
    textAlign: 'center',
  },
  qrResult: {
    fontSize: 20,
    color: 'black',
    marginTop: 20,
    textAlign: 'center',
  },
  dataContainer: {
    marginHorizontal: 20,
    marginTop: 20,
    height: 'auto',
    borderWidth: 1,
    borderColor: 'gray',
    borderRadius: 10,
    padding: 10,
  },
  btn: {
    height: 40,
    marginHorizontal: 20,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: '#b3def3ac',
    borderRadius: 10,
    marginTop: 20,
  },
});

export default App;
