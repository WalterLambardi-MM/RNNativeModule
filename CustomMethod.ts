import { NativeModules } from 'react-native';

const { CustomMethods } = NativeModules;

interface CustomMethodsProps {
  scanQRCode: () => Promise<string>;
}

export default CustomMethods as CustomMethodsProps;
