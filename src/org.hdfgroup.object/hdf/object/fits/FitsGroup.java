/*****************************************************************************
 * Copyright by The HDF Group.                                               *
 * Copyright by the Board of Trustees of the University of Illinois.         *
 * All rights reserved.                                                      *
 *                                                                           *
 * This file is part of the HDF Java Products distribution.                  *
 * The full copyright notice, including terms governing use, modification,   *
 * and redistribution, is contained in the files COPYING and Copyright.html. *
 * COPYING can be found at the root of the source code distribution tree.    *
 * Or, see https://support.hdfgroup.org/products/licenses.html               *
 * If you do not have access to either file, you may request a copy from     *
 * help@hdfgroup.org.                                                        *
 ****************************************************************************/

package hdf.object.fits;

import java.util.List;

import hdf.object.FileFormat;
import hdf.object.Group;

/**
 * An H5Group represents HDF5 group, inheriting from Group.
 * Every HDF5 object has at least one name. An HDF5 group is used to store
 * a set of the names together in one place, i.e. a group. The general
 * structure of a group is similar to that of the UNIX file system in
 * that the group may contain references to other groups or data objects
 * just as the UNIX directory may contain subdirectories or files.
 * <p>
 * @version 1.1 9/4/2007
 * @author Peter X. Cao
 */
public class FitsGroup extends Group
{
    private static final long serialVersionUID = 4993155577664991838L;

    /**
     * The list of attributes of this data object. Members of the list are
     * instance of Attribute.
     */
    private List attributeList;

    /** The default object ID for HDF5 objects */
    private static final long[] DEFAULT_OID = {0};

    /**
     * Constructs an HDF5 group with specific name, path, and parent.
     * <p>
     * @param fileFormat the file which containing the group.
     * @param name the name of this group.
     * @param path the full path of this group.
     * @param parent the parent of this group.
     * @param theID the unique identifier of this data object.
     */
    public FitsGroup(FileFormat fileFormat, String name, String path, Group parent, long[] theID) {
        super (fileFormat, name, path, parent, ((theID == null) ? DEFAULT_OID : theID));
    }

    /*
     * (non-Javadoc)
     * @see hdf.object.DataFormat#hasAttribute()
     */
    public boolean hasAttribute () { return false; }

    // Implementing DataFormat
    public List getMetadata() throws Exception {
        if (!isRoot()) {
            return null; // there is only one group in the file: the root
        }

        if (attributeList != null) {
            return attributeList;
        }

        return attributeList;
    }

    /**
     * Creates a new attribute and attached to this dataset if attribute does
     * not exist. Otherwise, just update the value of the attribute.
     *
     * @param info the atribute to attach
     */
    public void writeMetadata(Object info) throws Exception {
        // not supported
        throw new UnsupportedOperationException("Unsupported operation for FITS.");
    }

    /**
     * Deletes an attribute from this dataset.
     * <p>
     * @param info the attribute to delete.
     */
    public void removeMetadata(Object info) throws Exception {
        // not supported
        throw new UnsupportedOperationException("Unsupported operation for FITS.");
    }

    /*
     * (non-Javadoc)
     * @see hdf.object.DataFormat#updateMetadata(java.lang.Object)
     */
    public void updateMetadata(Object info) throws Exception {
        // not supported
        throw new UnsupportedOperationException("Unsupported operation for FITS.");
    }

    // Implementing DataFormat
    @Override
    public long open() {
        // not supported
        throw new UnsupportedOperationException("Unsupported operation for FITS.");
    }

    /** close group access */
    @Override
    public void close(long gid) {
        // not supported
        throw new UnsupportedOperationException("Unsupported operation for FITS.");
    }

    /**
     * Creates a new group.
     * @param name the name of the group to create.
     * @param pgroup the parent group of the new group.
     *
     * @return the new group if successful. Otherwise returns null.
     *
     * @throws Exception
     *            if there is an error
     */
    public static FitsGroup create(String name, Group pgroup)
        throws Exception {
        // not supported
        throw new UnsupportedOperationException("Unsupported operation for FITS.");
    }

    //Implementing DataFormat
    public List getMetadata(int... attrPropList) throws Exception {
        throw new UnsupportedOperationException("getMetadata(int... attrPropList) is not supported");
    }

}
