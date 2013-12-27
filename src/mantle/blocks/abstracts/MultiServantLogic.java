package mantle.blocks.abstracts;

import mantle.debug.DebugData;
import mantle.debug.IDebuggable;
import mantle.world.CoordTuple;
import mantle.blocks.iface.IMasterLogic;
import mantle.blocks.iface.IServantLogic;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;


public class MultiServantLogic extends TileEntity implements IServantLogic, IDebuggable
{
    boolean hasMaster;
    CoordTuple master;
    short masterID;
    byte masterMeat; //Typo, it stays!

    public boolean canUpdate ()
    {
        return false;
    }

    public boolean hasValidMaster ()
    {
        if (!hasMaster)
            return false;

        if (worldObj.getBlockId(master.x, master.y, master.z) == masterID && worldObj.getBlockMetadata(master.x, master.y, master.z) == masterMeat)
            return true;

        else
        {
            hasMaster = false;
            master = null;
            return false;
        }
    }

    public CoordTuple getMasterPosition ()
    {
        return master;
    }

    public void overrideMaster (int x, int y, int z)
    {
        hasMaster = true;
        master = new CoordTuple(x, y, z);
        masterID = (short) worldObj.getBlockId(x, y, z);
        masterMeat = (byte) worldObj.getBlockMetadata(x, y, z);
    }

    public void removeMaster ()
    {
        hasMaster = false;
        master = null;
        masterID = 0;
        masterMeat = 0;
    }

    @Deprecated
    public boolean verifyMaster (IMasterLogic logic, int x, int y, int z)
    {
        if (master.equalCoords(x, y, z) && worldObj.getBlockId(x, y, z) == masterID && worldObj.getBlockMetadata(x, y, z) == masterMeat)
            return true;
        else
            return false;
    }

    @Deprecated
    public boolean setMaster (int x, int y, int z)
    {
        if (!hasMaster || worldObj.getBlockId(master.x, master.y, master.z) != masterID || (worldObj.getBlockMetadata(master.x, master.y, master.z) != masterMeat))
        {
            overrideMaster(x, y, z);
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public boolean setPotentialMaster (IMasterLogic master, World world, int x, int y, int z)
    {
        return !hasMaster;
    }

    @Override
    public boolean verifyMaster (IMasterLogic logic, World world, int x, int y, int z)
    {
        if (hasMaster)
        {
            return hasValidMaster();
        }
        else
        {
            overrideMaster(x, y, z);
            return true;
        }
    }

    @Override
    public void invalidateMaster (IMasterLogic master, World world, int x, int y, int z)
    {
        hasMaster = false;
        master = null;
    }

    public void notifyMasterOfChange ()
    {
        if (hasValidMaster())
        {
            IMasterLogic logic = (IMasterLogic) worldObj.getBlockTileEntity(master.x, master.y, master.z);
            logic.notifyChange(this, xCoord, yCoord, zCoord);
        }
    }

    public void readCustomNBT (NBTTagCompound tags)
    {
        hasMaster = tags.getBoolean("TiedToMaster");
        if (hasMaster)
        {
            int xCenter = tags.getInteger("xCenter");
            int yCenter = tags.getInteger("yCenter");
            int zCenter = tags.getInteger("zCenter");
            master = new CoordTuple(xCenter, yCenter, zCenter);
            masterID = tags.getShort("MasterID");
            masterMeat = tags.getByte("masterMeat");
        }
    }

    public void writeCustomNBT (NBTTagCompound tags)
    {
        tags.setBoolean("TiedToMaster", hasMaster);
        if (hasMaster)
        {
            tags.setInteger("xCenter", master.x);
            tags.setInteger("yCenter", master.y);
            tags.setInteger("zCenter", master.z);
            tags.setShort("MasterID", masterID);
            tags.setByte("masterMeat", masterMeat);
        }
    }

    @Override
    public void readFromNBT (NBTTagCompound tags)
    {
        super.readFromNBT(tags);
        readCustomNBT(tags);
    }

    @Override
    public void writeToNBT (NBTTagCompound tags)
    {
        super.writeToNBT(tags);
        writeCustomNBT(tags);
    }

    /* Packets */
    @Override
    public Packet getDescriptionPacket ()
    {
        NBTTagCompound tag = new NBTTagCompound();
        writeCustomNBT(tag);
        return new Packet132TileEntityData(xCoord, yCoord, zCoord, 1, tag);
    }

    @Override
    public void onDataPacket (INetworkManager net, Packet132TileEntityData packet)
    {
        readCustomNBT(packet.data);
        worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
    }

    /* IDebuggable */
    @Override
    public DebugData getDebugInfo(EntityPlayer player) {
        String[] strs = new String[2];
        strs[0] = "Location: x" + xCoord + ", y" + yCoord + ", z" + zCoord;
        if (hasMaster) {
            strs[1] = "masterID: " + masterID + ", masterMeat: " + masterMeat;
        } else {
            strs[1] = "No active master.";
        }
        return new DebugData(player, getClass(), strs);
    }

}