using Android.OS;
using Android.Text;
using Android.Text.Method;
using Android.Views;
using Android.Widget;
using AndroidX.Fragment.App;

namespace MyDEFCON.Fragments
{
    public class AboutFragment : Fragment
    {
        public override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);
        }

        public override View OnCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            var ignored = base.OnCreateView(inflater, container, savedInstanceState);
            return inflater.Inflate(Resource.Layout.about_fragment, null);
        }

        public override void OnViewCreated(View view, Bundle savedInstanceState)
        {
            base.OnViewCreated(view, savedInstanceState);
            view.FindViewById<TextView>(Resource.Id.versionNameTextView).Text = Context.PackageManager.GetPackageInfo(Context.PackageName, 0).VersionName;
            var textView4 = view.FindViewById<TextView>(Resource.Id.textView4);
            if (Build.VERSION.SdkInt >= BuildVersionCodes.N) textView4.TextFormatted = Html.FromHtml("<a href=\"mailto:code_m@outlook.de\">technical support/feedback email</a>", FromHtmlOptions.ModeLegacy);
            else textView4.TextFormatted = Html.FromHtml("<a href=\"mailto:code_m@outlook.de\">technical support/feedback email</a>");
            textView4.MovementMethod = LinkMovementMethod.Instance;
        }

        public static AboutFragment NewInstance()
        {
            var aboutFragment = new AboutFragment { Arguments = new Bundle() };
            return aboutFragment;
        }
    }
}