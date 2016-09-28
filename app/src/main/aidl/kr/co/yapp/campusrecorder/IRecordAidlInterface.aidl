// IRecordAidlInterface.aidl
package kr.co.yapp.campusrecorder;

// Declare any non-default types here with import statements

interface IRecordAidlInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
            void play();
            void stop();
            void pause();
            void stateChange(int state);
            void init(String sName,String rName);
            int getServiceState();
}
