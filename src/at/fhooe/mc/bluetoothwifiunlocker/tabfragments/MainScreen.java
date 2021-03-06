/**
 * Holds and displays the current status information. 
 */
package at.fhooe.mc.bluetoothwifiunlocker.tabfragments;

import com.actionbarsherlock.app.SherlockFragment;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import at.fhooe.mc.bluetoothwifiunlocker.MyAdmin;
import at.fhooe.mc.bluetoothwifiunlocker.utils.Utils;
import at.fhooe.mc.bluetootwifiunlocker.R;

public class MainScreen extends SherlockFragment implements
		OnCheckedChangeListener {

	private TextView locked;
	private TextView netw_dev;
	private CheckBox lockNow;
	private TextView by;
	private Utils utils;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		return inflater.inflate(R.layout.main_screen, container, false);
	}

	/**
	 * Initializes the main-layout with the curren status from the
	 * SharedPreferences.
	 */
	@Override
	public void onResume() {
		super.onResume();

		locked = (TextView) getActivity().findViewById(R.id.lockmode);
		netw_dev = (TextView) getActivity().findViewById(R.id.netw_device);
		lockNow = (CheckBox) getActivity().findViewById(R.id.cb_lock_now);
		lockNow.setOnCheckedChangeListener(this);
		by = (TextView) getActivity().findViewById(R.id.by);

		SharedPreferences settings = getActivity().getSharedPreferences(
				"BT_WIFI", 0);
		String lockState = settings.getString("lockState", "locked");
		String network = settings.getString("network", "-1");
		String device = settings.getString("device", "-1");

		if (lockState.equals("unlocked")) {

			if (network.equals("-1") && !device.equals("-1")) {
				by.setText(R.string.by_device);
				netw_dev.setText(device);
			} else if (device.equals("-1") && !network.equals("-1")) {
				by.setText(R.string.by_wifi);
				netw_dev.setText(network);
			}
			netw_dev.setVisibility(View.VISIBLE);
			lockNow.setVisibility(View.VISIBLE);
			locked.setText(R.string.lock_state_unlocked);
		} else if (lockState.equals("locked")) {
			netw_dev.setText("");
			by.setText("");
			lockNow.setVisibility(View.INVISIBLE);
			lockNow.setChecked(false);
			locked.setText(R.string.lock_state_locked);
		}
	}

	/**
	 * Initializes the main-layout with the curren status from the
	 * SharedPreferences.
	 */
	@Override
	public void onStart() {
		super.onStart();

		locked = (TextView) getActivity().findViewById(R.id.lockmode);
		netw_dev = (TextView) getActivity().findViewById(R.id.netw_device);
		lockNow = (CheckBox) getActivity().findViewById(R.id.cb_lock_now);
		lockNow.setOnCheckedChangeListener(this);

		SharedPreferences settings = getActivity().getSharedPreferences(
				"BT_WIFI", 0);
		String lockState = settings.getString("lockState", "locked");
		String network = settings.getString("network", "-1");
		String device = settings.getString("device", "-1");

		if (lockState.equals("unlocked")) {
			if (network.equals("-1") && !device.equals("-1")) {
				netw_dev.setText("Bluetooth- Device " + device);
			} else if (device.equals("-1") && !network.equals("-1")) {
				netw_dev.setText(R.string.by_wifi + network);
			}
			netw_dev.setVisibility(View.VISIBLE);
			lockNow.setVisibility(View.VISIBLE);
			locked.setText(R.string.lock_state_unlocked);
		} else if (lockState.equals("locked")) {
			netw_dev.setText("");
			lockNow.setVisibility(View.INVISIBLE);
			lockNow.setChecked(false);
			locked.setText(R.string.lock_state_locked);
		}
	}

	/**
	 * This method checks if the user selected the lock-now Checkbox. If the
	 * CheckBox gets checked, the device gets locked immediately, the new status
	 * is saved in the SharedPreferences and the Notification is updated.
	 */
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (buttonView.getId() == lockNow.getId() && isChecked) {
			DevicePolicyManager deviceManger = (DevicePolicyManager) getActivity()
					.getSystemService(Context.DEVICE_POLICY_SERVICE);

			ComponentName compName = new ComponentName(getActivity(),
					MyAdmin.class);

			boolean active = deviceManger.isAdminActive(compName);

			if (active) {
				deviceManger.lockNow();

				SharedPreferences settings = getActivity()
						.getSharedPreferences("BT_WIFI", 0);
				Editor e = settings.edit();
				e.putString("lockState", "locked");
				e.commit();
				
				lockNow.setChecked(false);

				utils = new Utils();
				utils.showNotification(false, getActivity(), "MainScreen");
			}
		}
	}
}
