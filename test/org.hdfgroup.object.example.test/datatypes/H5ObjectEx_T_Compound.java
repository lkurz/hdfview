/************************************************************
  This example shows how to read and write compound
  datatypes to a dataset.  The program first writes
  compound structures to a dataset with a dataspace of DIM0,
  then closes the file.  Next, it reopens the file, reads
  back the data, and outputs it to the screen.
 ************************************************************/

package datatypes;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

import hdf.object.FileFormat;
import hdf.object.h5.H5CompoundDS;
import hdf.object.h5.H5File;

import hdf.hdf5lib.H5;
import hdf.hdf5lib.HDF5Constants;

public class H5ObjectEx_T_Compound {
    private static String FILENAME           = "H5ObjectEx_T_Compound.h5";
    private static String DATASETNAME        = "DS1";
    private static final int DIM0            = 4;
    private static final int RANK            = 1;
    protected static final int INTEGERSIZE   = 4;
    protected static final int DOUBLESIZE    = 8;
    protected final static int MAXSTRINGSIZE = 80;

    static class Sensor_Datatype {
        static int numberMembers = 4;
        static int[] memberDims  = {1, 1, 1, 1};

        static String[] memberNames   = {"Serial number", "Location", "Temperature (F)", "Pressure (inHg)"};
        static long[] memberMemTypes  = {HDF5Constants.H5T_NATIVE_INT, HDF5Constants.H5T_C_S1,
                                         HDF5Constants.H5T_NATIVE_DOUBLE, HDF5Constants.H5T_NATIVE_DOUBLE};
        static long[] memberFileTypes = {HDF5Constants.H5T_STD_I32BE, HDF5Constants.H5T_C_S1,
                                         HDF5Constants.H5T_IEEE_F64BE, HDF5Constants.H5T_IEEE_F64BE};
        static int[] memberStorage    = {INTEGERSIZE, MAXSTRINGSIZE, DOUBLESIZE, DOUBLESIZE};

        // Data size is the storage size for the members.
        static int getTotalDataSize()
        {
            int data_size = 0;
            for (int indx = 0; indx < numberMembers; indx++)
                data_size += memberStorage[indx] * memberDims[indx];
            return DIM0 * data_size;
        }

        static int getDataSize()
        {
            int data_size = 0;
            for (int indx = 0; indx < numberMembers; indx++)
                data_size += memberStorage[indx] * memberDims[indx];
            return data_size;
        }

        static int getOffset(int memberItem)
        {
            int data_offset = 0;
            for (int indx = 0; indx < memberItem; indx++)
                data_offset += memberStorage[indx];
            return data_offset;
        }
    }

    static class Sensor {
        public int serial_no;
        public String location;
        public double temperature;
        public double pressure;

        Sensor(int serial_no, String location, double temperature, double pressure)
        {
            this.serial_no   = serial_no;
            this.location    = location;
            this.temperature = temperature;
            this.pressure    = pressure;
        }

        Sensor(ByteBuffer databuf, int dbposition) { readBuffer(databuf, dbposition); }

        void writeBuffer(ByteBuffer databuf, int dbposition)
        {
            databuf.putInt(dbposition + Sensor_Datatype.getOffset(0), serial_no);
            byte[] temp_str = location.getBytes(Charset.forName("UTF-8"));
            int arraylen    = (temp_str.length > MAXSTRINGSIZE) ? MAXSTRINGSIZE : temp_str.length;
            for (int ndx = 0; ndx < arraylen; ndx++)
                databuf.put(dbposition + Sensor_Datatype.getOffset(1) + ndx, temp_str[ndx]);
            for (int ndx = arraylen; ndx < MAXSTRINGSIZE; ndx++)
                databuf.put(dbposition + Sensor_Datatype.getOffset(1) + arraylen, (byte)0);
            databuf.putDouble(dbposition + Sensor_Datatype.getOffset(2), temperature);
            databuf.putDouble(dbposition + Sensor_Datatype.getOffset(3), pressure);
        }

        void readBuffer(ByteBuffer databuf, int dbposition)
        {
            this.serial_no       = databuf.getInt(dbposition + Sensor_Datatype.getOffset(0));
            ByteBuffer stringbuf = databuf.duplicate();
            stringbuf.position(dbposition + Sensor_Datatype.getOffset(1));
            stringbuf.limit(dbposition + Sensor_Datatype.getOffset(1) + MAXSTRINGSIZE);
            byte[] bytearr = new byte[stringbuf.remaining()];
            stringbuf.get(bytearr);
            this.location    = new String(bytearr, Charset.forName("UTF-8")).trim();
            this.temperature = databuf.getDouble(dbposition + Sensor_Datatype.getOffset(2));
            this.pressure    = databuf.getDouble(dbposition + Sensor_Datatype.getOffset(3));
        }

        @Override
        public String toString()
        {
            return String.format("Serial number   : " + serial_no + "%n"
                                 + "Location        : " + location + "%n"
                                 + "Temperature (F) : " + temperature + "%n"
                                 + "Pressure (inHg) : " + pressure + "%n");
        }
    }

    private static void CreateDataset()
    {
        H5File file          = null;
        long file_id         = -1;
        long strtype_id      = -1;
        long memtype_id      = -1;
        long filetype_id     = -1;
        long dataspace_id    = -1;
        long dataset_id      = -1;
        long[] dims          = {DIM0};
        Sensor[] object_data = new Sensor[DIM0];
        byte[] dset_data     = null;

        // Initialize data.
        object_data[0] = new Sensor(1153, new String("Exterior (static)"), 53.23, 24.57);
        object_data[1] = new Sensor(1184, new String("Intake"), 55.12, 22.95);
        object_data[2] = new Sensor(1027, new String("Intake manifold"), 103.55, 31.23);
        object_data[3] = new Sensor(1313, new String("Exhaust manifold"), 1252.89, 84.11);

        // Create a new file using default properties.
        try {
            file    = new H5File(FILENAME, FileFormat.CREATE);
            file_id = file.open();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // Create string datatype.
        try {
            strtype_id = H5.H5Tcopy(HDF5Constants.H5T_C_S1);
            if (strtype_id >= 0)
                H5.H5Tset_size(strtype_id, MAXSTRINGSIZE);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // Create the compound datatype for memory.
        try {
            memtype_id = H5.H5Tcreate(HDF5Constants.H5T_COMPOUND, Sensor_Datatype.getDataSize());
            if (memtype_id >= 0) {
                for (int indx = 0; indx < Sensor_Datatype.numberMembers; indx++) {
                    long type_id = Sensor_Datatype.memberMemTypes[indx];
                    if (type_id == HDF5Constants.H5T_C_S1)
                        type_id = strtype_id;
                    H5.H5Tinsert(memtype_id, Sensor_Datatype.memberNames[indx],
                                 Sensor_Datatype.getOffset(indx), type_id);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // Create the compound datatype for the file. Because the standard
        // types we are using for the file may have different sizes than
        // the corresponding native types, we must manually calculate the
        // offset of each member.
        try {
            filetype_id = H5.H5Tcreate(HDF5Constants.H5T_COMPOUND, Sensor_Datatype.getDataSize());
            if (filetype_id >= 0) {
                for (int indx = 0; indx < Sensor_Datatype.numberMembers; indx++) {
                    long type_id = Sensor_Datatype.memberFileTypes[indx];
                    if (type_id == HDF5Constants.H5T_C_S1)
                        type_id = strtype_id;
                    H5.H5Tinsert(filetype_id, Sensor_Datatype.memberNames[indx],
                                 Sensor_Datatype.getOffset(indx), type_id);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // Create dataspace. Setting maximum size to NULL sets the maximum
        // size to be the current size.
        try {
            dataspace_id = H5.H5Screate_simple(RANK, dims, null);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // Create the dataset.
        try {
            if ((file_id >= 0) && (dataspace_id >= 0) && (filetype_id >= 0))
                dataset_id =
                    H5.H5Dcreate(file_id, DATASETNAME, filetype_id, dataspace_id, HDF5Constants.H5P_DEFAULT,
                                 HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // Write the compound data to the dataset.
        // allocate memory for read buffer.
        dset_data         = new byte[(int)dims[0] * Sensor_Datatype.getDataSize()];
        ByteBuffer outBuf = ByteBuffer.wrap(dset_data);
        outBuf.order(ByteOrder.nativeOrder());
        for (int indx = 0; indx < (int)dims[0]; indx++) {
            object_data[indx].writeBuffer(outBuf, indx * Sensor_Datatype.getDataSize());
        }
        try {
            if ((dataset_id >= 0) && (memtype_id >= 0))
                H5.H5Dwrite(dataset_id, memtype_id, HDF5Constants.H5S_ALL, HDF5Constants.H5S_ALL,
                            HDF5Constants.H5P_DEFAULT, dset_data);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // End access to the dataset and release resources used by it.
        try {
            if (dataset_id >= 0)
                H5.H5Dclose(dataset_id);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // Terminate access to the data space.
        try {
            if (dataspace_id >= 0)
                H5.H5Sclose(dataspace_id);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // Terminate access to the file type.
        try {
            if (filetype_id >= 0)
                H5.H5Tclose(filetype_id);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // Terminate access to the mem type.
        try {
            if (memtype_id >= 0)
                H5.H5Tclose(memtype_id);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (strtype_id >= 0)
                H5.H5Tclose(strtype_id);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // Close the file.
        try {
            file.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void ReadDataset()
    {
        H5File file       = null;
        H5CompoundDS dset = null;
        long strtype_id   = -1;
        long memtype_id   = -1;
        long dataspace_id = -1;
        long dataset_id   = -1;
        long[] dims       = {DIM0};
        Sensor[] object_data2;
        byte[] dset_data;

        // Open an existing file.
        try {
            file = new H5File(FILENAME, FileFormat.READ);
            file.open();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // Open an existing dataset.
        try {
            dset       = (H5CompoundDS)file.get(DATASETNAME);
            dataset_id = dset.open();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // Get dataspace and allocate memory for read buffer.
        try {
            if (dataset_id >= 0)
                dataspace_id = H5.H5Dget_space(dataset_id);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (dataspace_id >= 0)
                H5.H5Sget_simple_extent_dims(dataspace_id, dims, null);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // Create string datatype.
        try {
            strtype_id = H5.H5Tcopy(HDF5Constants.H5T_C_S1);
            if (strtype_id >= 0)
                H5.H5Tset_size(strtype_id, MAXSTRINGSIZE);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // Create the compound datatype for memory.
        try {
            memtype_id = H5.H5Tcreate(HDF5Constants.H5T_COMPOUND, Sensor_Datatype.getDataSize());
            if (memtype_id >= 0) {
                for (int indx = 0; indx < Sensor_Datatype.numberMembers; indx++) {
                    long type_id = Sensor_Datatype.memberMemTypes[indx];
                    if (type_id == HDF5Constants.H5T_C_S1)
                        type_id = strtype_id;
                    H5.H5Tinsert(memtype_id, Sensor_Datatype.memberNames[indx],
                                 Sensor_Datatype.getOffset(indx), type_id);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // allocate memory for read buffer.
        byte[] read_data = new byte[(int)dims[0] * Sensor_Datatype.getDataSize()];

        object_data2 = new Sensor[(int)dims[0]];

        // Read data.
        try {
            if ((dataset_id >= 0) && (memtype_id >= 0))
                H5.H5Dread(dataset_id, memtype_id, HDF5Constants.H5S_ALL, HDF5Constants.H5S_ALL,
                           HDF5Constants.H5P_DEFAULT, read_data);

            ByteBuffer inBuf = ByteBuffer.wrap(read_data);
            inBuf.order(ByteOrder.nativeOrder());
            for (int indx = 0; indx < (int)dims[0]; indx++) {
                object_data2[indx] = new Sensor(inBuf, indx * Sensor_Datatype.getDataSize());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // Output the data to the screen.
        for (int indx = 0; indx < dims[0]; indx++) {
            System.out.println(DATASETNAME + " [" + indx + "]:");
            System.out.println(object_data2[indx].toString());
        }
        System.out.println();

        try {
            if (dataset_id >= 0)
                dset.close(dataset_id);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // Terminate access to the data space.
        try {
            if (dataspace_id >= 0)
                H5.H5Sclose(dataspace_id);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // Terminate access to the mem type.
        try {
            if (memtype_id >= 0)
                H5.H5Tclose(memtype_id);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (strtype_id >= 0)
                H5.H5Tclose(strtype_id);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // Close the file.
        try {
            file.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        H5ObjectEx_T_Compound.CreateDataset();
        // Now we begin the read section of this example. Here we assume
        // the dataset and array have the same name and rank, but can have
        // any size. Therefore we must allocate a new array to read in
        // data using malloc().
        H5ObjectEx_T_Compound.ReadDataset();
    }
}
